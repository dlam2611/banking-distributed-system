package model;

import protocol.Command;
import protocol.MessageType;

import java.io.Serializable;
import java.util.UUID;

/**
 * Envelope Request dùng chung qua Socket.
 * Load Balancer chỉ cần đọc command và accountId để định tuyến (Consistent Hashing).
 * Dữ liệu chi tiết của từng nghiệp vụ nằm trong payload (chuỗi JSON).
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private MessageType messageType;
    private Command command;
    private String accountId;   // Routing key cho Load Balancer
    private String payload;     // Chuỗi JSON chứa DTO nghiệp vụ tương ứng
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

    public Request(Command command, String accountId, String payload) {
        this(command, accountId);
        this.payload = payload;
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
        return requestId != null ? requestId : "DEFAULT";
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", command=" + command +
                ", accountId='" + accountId + '\'' +
                ", payload='" + payload + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
