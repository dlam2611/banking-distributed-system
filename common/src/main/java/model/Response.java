package model;

import protocol.MessageType;
import protocol.Status;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private MessageType messageType;
    private Status status;
    private String message;
    private String data;
    private String serverNodeId;
    private long timestamp;

    public Response() {
        this.messageType = MessageType.RESPONSE;
        this.timestamp = System.currentTimeMillis();
    }

    public Response(String requestId, Status status, String message) {
        this();
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }

    public Response(String requestId, Status status, String message, String data) {
        this(requestId, status, message);
        this.data = data;
    }

    public static Response success(String requestId, String message, String data) {
        return new Response(requestId, Status.SUCCESS, message, data);
    }

    public static Response error(String requestId, Status status, String message) {
        return new Response(requestId, status, message, null);
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(String serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestId='" + requestId + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", serverNodeId='" + serverNodeId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
