package model;

import protocol.Command;
import protocol.MessageType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private MessageType messageType;
    private Command command;
    private String accountId;
    private String targetAccountId;
    private BigDecimal amount;
    private String username;
    private String password;
    private String payload;
    private long timestamp;

    public Request() {
        this.requestId = UUID.randomUUID().toString();
        this.messageType = MessageType.REQUEST;
        this.timestamp = System.currentTimeMillis();
    }

    public Request(Command command, String accountId) {
        this();
        this.command = command;
        this.accountId = accountId;
    }

    public Request(Command command, String accountId, BigDecimal amount) {
        this(command, accountId);
        this.amount = amount;
    }

    public Request(Command command, String accountId, String targetAccountId, BigDecimal amount) {
        this(command, accountId, amount);
        this.targetAccountId = targetAccountId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(String targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRoutingKey() {
        if (accountId != null && !accountId.trim().isEmpty()) {
            return accountId.trim();
        }
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }
        if (targetAccountId != null && !targetAccountId.trim().isEmpty()) {
            return targetAccountId.trim();
        }
        return requestId != null ? requestId : "DEFAULT";
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", command=" + command +
                ", accountId='" + accountId + '\'' +
                ", targetAccountId='" + targetAccountId + '\'' +
                ", amount=" + amount +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
