package protocol;

public enum Status {
    SUCCESS,
    FAILED,
    ERROR,
    UNAUTHORIZED,
    FORBIDDEN,
    INVALID_REQUEST,
    INVALID_INPUT,
    NOT_FOUND,
    ACCOUNT_NOT_FOUND,
    DUPLICATE,
    INSUFFICIENT_FUNDS,
    INTERNAL_ERROR,
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
