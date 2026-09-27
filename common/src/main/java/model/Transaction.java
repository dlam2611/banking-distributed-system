package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public Transaction() {
        this.status = "SUCCESS";
        this.createdAt = LocalDateTime.now();
    }

    public Transaction(String transactionId, String fromAccount, String toAccount,
                       BigDecimal amount, String transactionType, String status,
                       String description, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status != null ? status : "SUCCESS";
        this.description = description;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", fromAccount='" + fromAccount + '\'' +
                ", toAccount='" + toAccount + '\'' +
                ", amount=" + amount +
                ", transactionType='" + transactionType + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}