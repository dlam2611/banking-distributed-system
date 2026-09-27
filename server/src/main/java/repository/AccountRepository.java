package repository;

import model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class AccountRepository {

    public boolean create(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_id, full_name, cccd, phone, password, pin, balance, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountId());
            ps.setString(2, account.getFullName());
            ps.setString(3, account.getCccd());
            ps.setString(4, account.getPhone());
            ps.setString(5, account.getPassword());
            ps.setString(6, account.getPin());
            ps.setBigDecimal(7, account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO);
            ps.setString(8, account.getStatus() != null ? account.getStatus() : "ACTIVE");
            ps.setTimestamp(9, Timestamp.valueOf(account.getCreatedAt() != null ? account.getCreatedAt() : LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        }
    }

    public Account findById(String accountId) throws SQLException {
        String sql = "SELECT account_id, full_name, cccd, phone, password, pin, balance, status, created_at " +
                     "FROM accounts WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Account findByCccd(String cccd) throws SQLException {
        String sql = "SELECT account_id, full_name, cccd, phone, password, pin, balance, status, created_at " +
                     "FROM accounts WHERE cccd = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean existsById(String accountId) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE account_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByCccd(String cccd) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE cccd = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByPhone(String phone) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE phone = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Trừ tiền tài khoản nguồn trong giao dịch DB (Transaction-aware).
     * Điều kiện AND balance >= ? đảm bảo nguyên tử, chống rút âm tiền tuyệt đối.
     * Trả về số dòng cập nhật (1 là thành công, 0 là số dư không đủ).
     */
    public int debit(Connection conn, String accountId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, accountId);
            ps.setBigDecimal(3, amount);
            return ps.executeUpdate();
        }
    }

    /**
     * Cộng tiền tài khoản nhận trong giao dịch DB (Transaction-aware).
     * Trả về số dòng cập nhật (1 là thành công, 0 là thất bại).
     */
    public int credit(Connection conn, String accountId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, accountId);
            return ps.executeUpdate();
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();

        return new Account(
                rs.getString("account_id"),
                rs.getString("full_name"),
                rs.getString("cccd"),
                rs.getString("phone"),
                rs.getString("password"),
                rs.getString("pin"),
                rs.getBigDecimal("balance"),
                rs.getString("status"),
                createdAt
        );
    }
}
