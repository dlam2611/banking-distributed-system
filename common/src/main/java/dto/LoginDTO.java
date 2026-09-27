package dto;

import java.io.Serializable;

public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cccd;
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(String cccd, String password) {
        this.cccd = cccd;
        this.password = password;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
