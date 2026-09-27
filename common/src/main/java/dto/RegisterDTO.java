package dto;

import java.io.Serializable;

public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountId;
    private String fullName;
    private String cccd;
    private String phone;
    private String password;
    private String pin;

    public RegisterDTO() {
    }

    public RegisterDTO(String accountId, String fullName, String cccd, String phone,
                       String password, String pin) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.cccd = cccd;
        this.phone = phone;
        this.password = password;
        this.pin = pin;
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
}
