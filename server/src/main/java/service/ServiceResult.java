package service;

import protocol.Status;

public class ServiceResult {

    private final boolean success;
    private final Status status;
    private final String message;
    private final String data;

    public ServiceResult(boolean success, Status status, String message, String data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ServiceResult success(String message, String data) {
        return new ServiceResult(true, Status.SUCCESS, message, data);
    }

    public static ServiceResult error(Status status, String message) {
        return new ServiceResult(false, status, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
