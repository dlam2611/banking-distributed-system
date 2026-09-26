package protocol;

public enum Status {
    SUCCESS,
    FAILED,
    ERROR,
    UNAUTHORIZED,
    INVALID_REQUEST,
    ACCOUNT_NOT_FOUND,
    INSUFFICIENT_FUNDS,
    SERVER_UNAVAILABLE;

    public static Status fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return null;
    }
}
