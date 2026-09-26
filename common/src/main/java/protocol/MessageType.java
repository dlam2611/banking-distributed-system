package protocol;

public enum MessageType {
    REQUEST,
    RESPONSE,
    HEARTBEAT,
    NOTIFICATION,
    ERROR;

    public static MessageType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        for (MessageType type : MessageType.values()) {
            if (type.name().equalsIgnoreCase(text.trim())) {
                return type;
            }
        }
        return null;
    }
}
