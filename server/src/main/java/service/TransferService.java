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



    public ServiceResult transfer(String fromAccount, TransferDTO dto) {

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


        if (fromAccount.equals(toAccount)) {
            return ServiceResult.error(Status.INVALID_INPUT, "Không thể chuyển tiền cho chính tài khoản của bạn!");
        }


        BigDecimal amount = dto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số tiền chuyển phải lớn hơn 0!");
        }


        if (amount.scale() > 2) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số tiền chuyển không được vượt quá 2 chữ số thập phân!");
        }


        if (dto.getPin() == null || dto.getPin().trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Vui lòng nhập mã PIN giao dịch để xác thực!");
        }
        String inputPin = dto.getPin().trim();


        try {

            Account sender = accountRepository.findById(fromAccount);
            if (sender == null) {
                return ServiceResult.error(Status.NOT_FOUND, "Tài khoản người gửi không tồn tại trên hệ thống!");
            }
            if (!"ACTIVE".equalsIgnoreCase(sender.getStatus())) {
                return ServiceResult.error(Status.FORBIDDEN, "Tài khoản của bạn đang bị khóa hoặc ngưng hoạt động!");
            }


            if (!inputPin.equals(sender.getPin())) {
                return ServiceResult.error(Status.UNAUTHORIZED, "Mã PIN xác thực giao dịch không chính xác!");
            }


            if (sender.getBalance() == null || sender.getBalance().compareTo(amount) < 0) {
                return ServiceResult.error(Status.INSUFFICIENT_FUNDS,
                        "Số dư tài khoản không đủ để thực hiện giao dịch (Số dư hiện tại: " +
                        (sender.getBalance() != null ? sender.getBalance().toPlainString() : "0") + " VNĐ)!");
            }


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



        lockManager.lockAccounts(fromAccount, toAccount);
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


            int debitResult = accountRepository.debit(conn, fromAccount, amount);
            if (debitResult == 0) {
                conn.rollback();
                return ServiceResult.error(Status.INSUFFICIENT_FUNDS, "Số dư tài khoản không đủ để thực hiện giao dịch!");
            }


            int creditResult = accountRepository.credit(conn, toAccount, amount);
            if (creditResult == 0) {
                conn.rollback();
                return ServiceResult.error(Status.INTERNAL_ERROR, "Không thể ghi nhận tiền vào tài khoản người nhận!");
            }


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


            conn.commit();


            return ServiceResult.success(
                    "Chuyển thành công " + amount.toPlainString() + " VNĐ đến tài khoản " + toAccount + "!",
                    JsonUtil.toJson(tx)
            );

        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi thực hiện giao dịch chuyển tiền: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Không thể rollback giao dịch: " + rollbackEx.getMessage());
                }
            }
            return ServiceResult.error(Status.INTERNAL_ERROR, "Giao dịch thất bại do lỗi hệ thống: " + e.getMessage());

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }

            lockManager.unlockAccounts(fromAccount, toAccount);
        }
    }
}
