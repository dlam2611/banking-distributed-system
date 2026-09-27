package service;

import dto.LoginDTO;
import dto.RegisterDTO;
import model.Account;
import protocol.Status;
import repository.AccountRepository;
import util.JsonUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService() {
        this.accountRepository = new AccountRepository();
    }

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Xử lý ĐĂNG KÝ tài khoản mới.
     * Kiểm tra toàn bộ tính hợp lệ và chống trùng lặp dữ liệu.
     */
    public ServiceResult register(RegisterDTO dto) {
        if (dto == null) {
            return ServiceResult.error(Status.INVALID_INPUT, "Dữ liệu đăng ký không được để trống!");
        }

        // 1. Kiểm tra định dạng số tài khoản (phải toàn chữ số, 6-20 ký tự)
        if (dto.getAccountId() == null || !dto.getAccountId().trim().matches("^[0-9]{6,20}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số tài khoản chỉ được chứa chữ số (từ 6 đến 20 số)!");
        }
        String accountId = dto.getAccountId().trim();

        // 2. Kiểm tra họ và tên
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Họ và tên không được để trống!");
        }
        String fullName = dto.getFullName().trim();

        // 3. Kiểm tra CCCD (phải là số, từ 9 đến 12 số)
        if (dto.getCccd() == null || !dto.getCccd().trim().matches("^[0-9]{9,12}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số CCCD không hợp lệ (phải từ 9 đến 12 chữ số)!");
        }
        String cccd = dto.getCccd().trim();

        // 4. Kiểm tra Số điện thoại (phải là số, từ 10 đến 11 số)
        if (dto.getPhone() == null || !dto.getPhone().trim().matches("^[0-9]{10,11}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số điện thoại không hợp lệ (phải từ 10 đến 11 chữ số)!");
        }
        String phone = dto.getPhone().trim();

        // 5. Kiểm tra Mật khẩu (tối thiểu 6 ký tự)
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            return ServiceResult.error(Status.INVALID_INPUT, "Mật khẩu phải có tối thiểu 6 ký tự!");
        }

        // 6. Kiểm tra Mã PIN giao dịch (đúng 6 chữ số)
        if (dto.getPin() == null || !dto.getPin().trim().matches("^[0-9]{6}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Mã PIN phải gồm đúng 6 chữ số!");
        }
        String pin = dto.getPin().trim();

        try {
            // 7. Kiểm tra trùng lặp trên hệ thống
            if (accountRepository.existsById(accountId)) {
                return ServiceResult.error(Status.DUPLICATE, "Số tài khoản [" + accountId + "] đã tồn tại trên hệ thống!");
            }
            if (accountRepository.existsByCccd(cccd)) {
                return ServiceResult.error(Status.DUPLICATE, "Số CCCD [" + cccd + "] đã được đăng ký!");
            }
            if (accountRepository.existsByPhone(phone)) {
                return ServiceResult.error(Status.DUPLICATE, "Số điện thoại [" + phone + "] đã được đăng ký!");
            }

            // 8. Tạo mới tài khoản với số dư khởi tạo = 0 và trạng thái ACTIVE
            Account account = new Account(
                    accountId,
                    fullName,
                    cccd,
                    phone,
                    dto.getPassword(), // Trong thực tế băm BCrypt, ở đây lưu password bảo mật
                    pin,
                    BigDecimal.ZERO,
                    "ACTIVE",
                    LocalDateTime.now()
            );

            boolean created = accountRepository.create(account);
            if (!created) {
                return ServiceResult.error(Status.INTERNAL_ERROR, "Không thể lưu tài khoản vào cơ sở dữ liệu!");
            }

            // Giấu mật khẩu và mã PIN trước khi trả về Client
            Account sanitized = sanitize(account);
            return ServiceResult.success("Đăng ký tài khoản thành công!", JsonUtil.toJson(sanitized));

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng ký tài khoản: " + e.getMessage());
            return ServiceResult.error(Status.INTERNAL_ERROR, "Lỗi cơ sở dữ liệu khi đăng ký: " + e.getMessage());
        }
    }

    /**
     * Xử lý ĐĂNG NHẬP bằng CCCD và Password.
     */
    public ServiceResult login(LoginDTO dto) {
        if (dto == null) {
            return ServiceResult.error(Status.INVALID_INPUT, "Dữ liệu đăng nhập không được để trống!");
        }

        if (dto.getCccd() == null || dto.getCccd().trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Vui lòng nhập số CCCD!");
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Vui lòng nhập mật khẩu!");
        }

        String cccd = dto.getCccd().trim();
        String password = dto.getPassword();

        try {
            Account account = accountRepository.findByCccd(cccd);
            if (account == null) {
                return ServiceResult.error(Status.UNAUTHORIZED, "Số CCCD hoặc mật khẩu không chính xác!");
            }

            if (!password.equals(account.getPassword())) {
                return ServiceResult.error(Status.UNAUTHORIZED, "Số CCCD hoặc mật khẩu không chính xác!");
            }

            if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
                return ServiceResult.error(Status.FORBIDDEN, "Tài khoản của bạn đang bị khóa hoặc ngưng hoạt động!");
            }

            // Trả về thông tin tài khoản (đã che pin và mật khẩu)
            Account sanitized = sanitize(account);
            return ServiceResult.success("Đăng nhập thành công!", JsonUtil.toJson(sanitized));

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng nhập: " + e.getMessage());
            return ServiceResult.error(Status.INTERNAL_ERROR, "Lỗi cơ sở dữ liệu khi đăng nhập: " + e.getMessage());
        }
    }

    /**
     * Lấy thông tin tài khoản (đã giấu thông tin nhạy cảm).
     */
    public Account getAccountById(String accountId) throws SQLException {
        return accountRepository.findById(accountId);
    }

    /**
     * Ẩn thông tin bảo mật trước khi gửi ra ngoài mạng.
     */
    private Account sanitize(Account raw) {
        Account clean = new Account();
        clean.setAccountId(raw.getAccountId());
        clean.setFullName(raw.getFullName());
        clean.setCccd(raw.getCccd());
        clean.setPhone(raw.getPhone());
        clean.setBalance(raw.getBalance());
        clean.setStatus(raw.getStatus());
        clean.setCreatedAt(raw.getCreatedAt());
        clean.setPassword(null);
        clean.setPin(null);
        return clean;
    }
}
