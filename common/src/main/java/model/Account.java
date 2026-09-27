package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountId;
    private String fullName;
    private String cccd;
    private String phone;
    private String password;
    private String pin;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;

    public Account() {
        this.balance = BigDecimal.ZERO;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }

    public Account(String accountId, String fullName, String cccd, String phone,
                   String password, String pin, BigDecimal balance, String status,
                   LocalDateTime createdAt) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.cccd = cccd;
        this.phone = phone;
        this.password = password;
        this.pin = pin;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.status = status != null ? status : "ACTIVE";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", cccd='" + cccd + '\'' +
                ", phone='" + phone + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}