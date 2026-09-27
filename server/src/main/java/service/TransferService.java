package service;

import dto.TransferDTO;
import lock.AccountLockManager;
import model.Account;
import model.Transaction;
import protocol.Status;
import repository.AccountRepository;
import repository.DBConnection;
import repository.TransactionRepository;
import util.JsonUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountLockManager lockManager;

    public TransferService() {
        this.accountRepository = new AccountRepository();
        this.transactionRepository = new TransactionRepository();
        this.lockManager = AccountLockManager.getInstance();
    }

    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           AccountLockManager lockManager) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.lockManager = lockManager;
    }

    /**
     * Xử lý CHUYỂN TIỀN nguyên tử (Atomic Transfer).
     * Đảm bảo:
     * 1. Kiểm tra toàn bộ tính hợp lệ đầu vào.
     * 2. Khóa tài khoản chống Race condition & Deadlock (Canonical Lock Ordering).
     * 3. Giao dịch CSDL (BEGIN ... COMMIT / ROLLBACK) trừ tiền A và cộng tiền B đồng thời.
     * 4. Ghi nhận biên lai giao dịch vào bảng transactions.
     */
    public ServiceResult transfer(String fromAccount, TransferDTO dto) {
        // --- 1. KIỂM TRA ĐẦU VÀO CƠ BẢN ---
        if (fromAccount == null || fromAccount.trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Không xác định được tài khoản người gửi!");
        }
        fromAccount = fromAccount.trim();

        if (dto == null) {
            return ServiceResult.error(Status.INVALID_INPUT, "Dữ liệu chuyển tiền không được để trống!");
        }

        if (dto.getToAccount() == null || dto.getToAccount().trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Vui lòng nhập số tài khoản người nhận!");
        }
        String toAccount = dto.getToAccount().trim();

        // 1.1. Chống tự chuyển tiền cho chính mình
        if (fromAccount.equals(toAccount)) {
            return ServiceResult.error(Status.INVALID_INPUT, "Không thể chuyển tiền cho chính tài khoản của bạn!");
        }

        // 1.2. Kiểm tra số tiền chuyển
        BigDecimal amount = dto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số tiền chuyển phải lớn hơn 0!");
        }

        // Kiểm tra không để quá 2 chữ số thập phân
        if (amount.scale() > 2) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số tiền chuyển không được vượt quá 2 chữ số thập phân!");
        }

        // 1.3. Kiểm tra mã PIN giao dịch
        if (dto.getPin() == null || dto.getPin().trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Vui lòng nhập mã PIN giao dịch để xác thực!");
        }
        String inputPin = dto.getPin().trim();

        // --- 2. KIỂM TRA THÔNG TIN TÀI KHOẢN VÀ BẢO MẬT TRƯỚC KHI KHÓA ---
        try {
            // 2.1. Kiểm tra tài khoản gửi
            Account sender = accountRepository.findById(fromAccount);
            if (sender == null) {
                return ServiceResult.error(Status.NOT_FOUND, "Tài khoản người gửi không tồn tại trên hệ thống!");
            }
            if (!"ACTIVE".equalsIgnoreCase(sender.getStatus())) {
                return ServiceResult.error(Status.FORBIDDEN, "Tài khoản của bạn đang bị khóa hoặc ngưng hoạt động!");
            }

            // 2.2. Xác thực mã PIN người gửi
            if (!inputPin.equals(sender.getPin())) {
                return ServiceResult.error(Status.UNAUTHORIZED, "Mã PIN xác thực giao dịch không chính xác!");
            }

            // 2.3. Kiểm tra số dư người gửi
            if (sender.getBalance() == null || sender.getBalance().compareTo(amount) < 0) {
                return ServiceResult.error(Status.INSUFFICIENT_FUNDS,
                        "Số dư tài khoản không đủ để thực hiện giao dịch (Số dư hiện tại: " +
                        (sender.getBalance() != null ? sender.getBalance().toPlainString() : "0") + " VNĐ)!");
            }

            // 2.4. Kiểm tra tài khoản người nhận
            Account receiver = accountRepository.findById(toAccount);
            if (receiver == null) {
                return ServiceResult.error(Status.NOT_FOUND, "Số tài khoản người nhận [" + toAccount + "] không tồn tại!");
            }
            if (!"ACTIVE".equalsIgnoreCase(receiver.getStatus())) {
                return ServiceResult.error(Status.FORBIDDEN, "Tài khoản người nhận đang bị khóa, không thể nhận tiền!");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra dữ liệu trước khi chuyển tiền: " + e.getMessage());
            return ServiceResult.error(Status.INTERNAL_ERROR, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }

        // --- 3. TIẾN HÀNH KHÓA TÀI KHOẢN & GIAO DỊCH CƠ SỞ DỮ LIỆU ---
        // Khóa 2 tài khoản theo thứ tự an toàn chống Deadlock
        lockManager.lockAccounts(fromAccount, toAccount);
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu DB Transaction

            // Bước 3.1: Trừ tiền người gửi (Có điều kiện atomic AND balance >= amount)
            int debitResult = accountRepository.debit(conn, fromAccount, amount);
            if (debitResult == 0) {
                conn.rollback();
                return ServiceResult.error(Status.INSUFFICIENT_FUNDS, "Số dư tài khoản không đủ để thực hiện giao dịch!");
            }

            // Bước 3.2: Cộng tiền người nhận
            int creditResult = accountRepository.credit(conn, toAccount, amount);
            if (creditResult == 0) {
                conn.rollback();
                return ServiceResult.error(Status.INTERNAL_ERROR, "Không thể ghi nhận tiền vào tài khoản người nhận!");
            }

            // Bước 3.3: Ghi nhận biên lai giao dịch vào bảng transactions
            String txId = UUID.randomUUID().toString();
            String description = dto.getDescription() != null && !dto.getDescription().trim().isEmpty()
                    ? dto.getDescription().trim()
                    : "Chuyen tien den " + toAccount;

            Transaction tx = new Transaction(
                    txId,
                    fromAccount,
                    toAccount,
                    amount,
                    "TRANSFER",
                    "SUCCESS",
                    description,
                    LocalDateTime.now()
            );

            boolean txSaved = transactionRepository.save(conn, tx);
            if (!txSaved) {
                conn.rollback();
                return ServiceResult.error(Status.INTERNAL_ERROR, "Không thể ghi nhận lịch sử giao dịch!");
            }

            // Bước 3.4: Cam kết hoàn tất giao dịch
            conn.commit();

            // Trả về biên lai giao dịch thành công cho Client
            return ServiceResult.success(
                    "Chuyển thành công " + amount.toPlainString() + " VNĐ đến tài khoản " + toAccount + "!",
                    JsonUtil.toJson(tx)
            );

        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi thực hiện giao dịch chuyển tiền: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback toàn bộ trạng thái tiền
                } catch (SQLException rollbackEx) {
                    System.err.println("Không thể rollback giao dịch: " + rollbackEx.getMessage());
                }
            }
            return ServiceResult.error(Status.INTERNAL_ERROR, "Giao dịch thất bại do lỗi hệ thống: " + e.getMessage());

        } finally {
            // Đóng kết nối DB
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
            // Mở khóa cả 2 tài khoản
            lockManager.unlockAccounts(fromAccount, toAccount);
        }
    }
}
