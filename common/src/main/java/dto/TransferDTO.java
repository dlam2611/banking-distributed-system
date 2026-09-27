package dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String toAccount;
    private BigDecimal amount;
    private String pin;
    private String description;

    public TransferDTO() {
    }

    public TransferDTO(String toAccount, BigDecimal amount, String pin, String description) {
        this.toAccount = toAccount;
        this.amount = amount;
        this.pin = pin;
        this.description = description;
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
