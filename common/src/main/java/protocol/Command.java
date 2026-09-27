package protocol;

public enum Command {
    REGISTER,
    LOGIN,
    LOGOUT,
    TRANSFER,
    CHECK_ACCOUNT,
    GET_TRANSACTIONS,
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
