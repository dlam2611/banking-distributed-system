package repository;

import model.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionRepository {

    /**
     * Lưu bản ghi giao dịch trong cùng Transaction DB.
     */
    public boolean save(Connection conn, Transaction tx) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, from_account, to_account, amount, transaction_type, status, description, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String txId = tx.getTransactionId() != null ? tx.getTransactionId() : UUID.randomUUID().toString();
            tx.setTransactionId(txId);

            ps.setString(1, txId);
            ps.setString(2, tx.getFromAccount());
            ps.setString(3, tx.getToAccount());
            ps.setBigDecimal(4, tx.getAmount());
            ps.setString(5, tx.getTransactionType() != null ? tx.getTransactionType() : "TRANSFER");
            ps.setString(6, tx.getStatus() != null ? tx.getStatus() : "SUCCESS");
            ps.setString(7, tx.getDescription());
            ps.setTimestamp(8, Timestamp.valueOf(tx.getCreatedAt() != null ? tx.getCreatedAt() : LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Lấy danh sách giao dịch gần nhất của tài khoản (gửi hoặc nhận).
     */
    public java.util.List<Transaction> findRecentByAccount(String accountId, int limit) {
        java.util.List<Transaction> list = new java.util.ArrayList<>();
        if (accountId == null || accountId.trim().isEmpty()) {
            return list;
        }

        String sql = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ps.setString(2, accountId);
            ps.setInt(3, limit > 0 ? limit : 10);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction tx = new Transaction();
                    tx.setTransactionId(rs.getString("transaction_id"));
                    tx.setFromAccount(rs.getString("from_account"));
                    tx.setToAccount(rs.getString("to_account"));
                    tx.setAmount(rs.getBigDecimal("amount"));
                    tx.setTransactionType(rs.getString("transaction_type"));
                    tx.setStatus(rs.getString("status"));
                    tx.setDescription(rs.getString("description"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        tx.setCreatedAt(ts.toLocalDateTime());
                    }
                    list.add(tx);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn lịch sử giao dịch: " + e.getMessage());
        }
        return list;
    }
}
