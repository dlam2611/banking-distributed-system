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
}
