package protocol;

public enum Command {
    LOGIN,
    LOGOUT,
    BALANCE,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    TRANSACTION_HISTORY,
    PING;

    public static Command fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (Command c : Command.values()) {
            if (c.name().equalsIgnoreCase(text.trim())) {
                return c;
            }
        }
        return null;
    }
}
