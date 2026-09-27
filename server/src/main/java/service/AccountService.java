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



    public ServiceResult register(RegisterDTO dto) {
        if (dto == null) {
            return ServiceResult.error(Status.INVALID_INPUT, "Dữ liệu đăng ký không được để trống!");
        }


        if (dto.getAccountId() == null || !dto.getAccountId().trim().matches("^[0-9]{6,20}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số tài khoản chỉ được chứa chữ số (từ 6 đến 20 số)!");
        }
        String accountId = dto.getAccountId().trim();


        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Họ và tên không được để trống!");
        }
        String fullName = dto.getFullName().trim();


        if (dto.getCccd() == null || !dto.getCccd().trim().matches("^[0-9]{9,12}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số CCCD không hợp lệ (phải từ 9 đến 12 chữ số)!");
        }
        String cccd = dto.getCccd().trim();


        if (dto.getPhone() == null || !dto.getPhone().trim().matches("^[0-9]{10,11}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số điện thoại không hợp lệ (phải từ 10 đến 11 chữ số)!");
        }
        String phone = dto.getPhone().trim();


        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            return ServiceResult.error(Status.INVALID_INPUT, "Mật khẩu phải có tối thiểu 6 ký tự!");
        }


        if (dto.getPin() == null || !dto.getPin().trim().matches("^[0-9]{6}$")) {
            return ServiceResult.error(Status.INVALID_INPUT, "Mã PIN phải gồm đúng 6 chữ số!");
        }
        String pin = dto.getPin().trim();

        try {

            if (accountRepository.existsById(accountId)) {
                return ServiceResult.error(Status.DUPLICATE, "Số tài khoản [" + accountId + "] đã tồn tại trên hệ thống!");
            }
            if (accountRepository.existsByCccd(cccd)) {
                return ServiceResult.error(Status.DUPLICATE, "Số CCCD [" + cccd + "] đã được đăng ký!");
            }
            if (accountRepository.existsByPhone(phone)) {
                return ServiceResult.error(Status.DUPLICATE, "Số điện thoại [" + phone + "] đã được đăng ký!");
            }


            String hashedPassword = util.PasswordUtil.hashPassword(dto.getPassword());


            Account account = new Account(
                    accountId,
                    fullName,
                    cccd,
                    phone,
                    hashedPassword,
                    pin,
                    BigDecimal.ZERO,
                    "ACTIVE",
                    LocalDateTime.now()
            );

            boolean created = accountRepository.create(account);
            if (!created) {
                return ServiceResult.error(Status.INTERNAL_ERROR, "Không thể lưu tài khoản vào cơ sở dữ liệu!");
            }


            Account sanitized = sanitize(account);
            return ServiceResult.success("Đăng ký tài khoản thành công!", JsonUtil.toJson(sanitized));

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng ký tài khoản: " + e.getMessage());
            return ServiceResult.error(Status.INTERNAL_ERROR, "Lỗi cơ sở dữ liệu khi đăng ký: " + e.getMessage());
        }
    }



    public ServiceResult login(LoginDTO dto) {
        if (dto == null) {
            return ServiceResult.error(Status.INVALID_INPUT, "Dữ liệu đăng nhập không được để trống!");
        }

        if (dto.getCccd() == null || dto.getCccd().trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Vui lòng nhập số CCCD hoặc Tên đăng nhập!");
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Vui lòng nhập mật khẩu!");
        }

        String identifier = dto.getCccd().trim();
        String password = dto.getPassword();

        try {

            Account account = accountRepository.findByCccd(identifier);
            if (account == null) {
                account = accountRepository.findById(identifier);
            }

            if (account == null) {
                return ServiceResult.error(Status.UNAUTHORIZED, "Số CCCD hoặc mật khẩu không chính xác!");
            }


            boolean passwordValid = util.PasswordUtil.checkPassword(password, account.getPassword());
            if (!passwordValid) {
                return ServiceResult.error(Status.UNAUTHORIZED, "Số CCCD hoặc mật khẩu không chính xác!");
            }


            if (!util.PasswordUtil.isBCryptHash(account.getPassword())) {
                try {
                    accountRepository.updatePassword(account.getAccountId(), util.PasswordUtil.hashPassword(password));
                } catch (Exception e) {
                    System.err.println("Cảnh báo: Không thể tự động nâng cấp hash mật khẩu: " + e.getMessage());
                }
            }

            if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
                return ServiceResult.error(Status.FORBIDDEN, "Tài khoản của bạn đang bị khóa hoặc ngưng hoạt động!");
            }


            Account sanitized = sanitize(account);
            return ServiceResult.success("Đăng nhập thành công!", JsonUtil.toJson(sanitized));

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng nhập: " + e.getMessage());
            return ServiceResult.error(Status.INTERNAL_ERROR, "Lỗi cơ sở dữ liệu khi đăng nhập: " + e.getMessage());
        }
    }



    public ServiceResult lookupAccount(String targetAccount) {
        if (targetAccount == null || targetAccount.trim().isEmpty()) {
            return ServiceResult.error(Status.INVALID_INPUT, "Số tài khoản tra cứu không được để trống!");
        }

        String query = targetAccount.trim();
        try {
            Account account = accountRepository.findById(query);
            if (account == null) {
                account = accountRepository.findByCccd(query);
            }

            if (account == null) {
                return ServiceResult.error(Status.ACCOUNT_NOT_FOUND, "Không tìm thấy thông tin tài khoản [" + query + "]!");
            }

            if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
                return ServiceResult.error(Status.FORBIDDEN, "Tài khoản [" + query + "] đang bị khóa hoặc ngưng hoạt động!");
            }

            Account sanitized = sanitize(account);
            return ServiceResult.success("Tra cứu tài khoản thành công!", JsonUtil.toJson(sanitized));
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tra cứu tài khoản: " + e.getMessage());
            return ServiceResult.error(Status.INTERNAL_ERROR, "Lỗi cơ sở dữ liệu khi tra cứu tài khoản: " + e.getMessage());
        }
    }



    public Account getAccountById(String accountId) throws SQLException {
        return accountRepository.findById(accountId);
    }



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
