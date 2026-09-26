package util;

import model.Account;
import model.Request;
import model.Response;
import model.Transaction;
import protocol.Command;
import protocol.MessageType;
import protocol.Status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class JsonUtil {

    private JsonUtil() {}

    public static String toJson(Object obj) {
        if (obj == null) return "null";

        if (obj instanceof Request) {
            Request r = (Request) obj;
            StringBuilder sb = new StringBuilder("{");
            appendField(sb, "requestId", r.getRequestId(), true);
            appendField(sb, "messageType", r.getMessageType() != null ? r.getMessageType().name() : null, false);
            appendField(sb, "command", r.getCommand() != null ? r.getCommand().name() : null, false);
            appendField(sb, "accountId", r.getAccountId(), false);
            appendField(sb, "targetAccountId", r.getTargetAccountId(), false);
            appendField(sb, "amount", r.getAmount() != null ? r.getAmount().toPlainString() : null, false);
            appendField(sb, "username", r.getUsername(), false);
            appendField(sb, "password", r.getPassword(), false);
            appendField(sb, "payload", r.getPayload(), false);
            sb.append(",\"timestamp\":").append(r.getTimestamp());
            sb.append("}");
            return sb.toString();
        }

        if (obj instanceof Response) {
            Response r = (Response) obj;
            StringBuilder sb = new StringBuilder("{");
            appendField(sb, "requestId", r.getRequestId(), true);
            appendField(sb, "messageType", r.getMessageType() != null ? r.getMessageType().name() : null, false);
            appendField(sb, "status", r.getStatus() != null ? r.getStatus().name() : null, false);
            appendField(sb, "message", r.getMessage(), false);
            appendField(sb, "data", r.getData(), false);
            appendField(sb, "serverNodeId", r.getServerNodeId(), false);
            sb.append(",\"timestamp\":").append(r.getTimestamp());
            sb.append("}");
            return sb.toString();
        }

        if (obj instanceof Account) {
            Account a = (Account) obj;
            StringBuilder sb = new StringBuilder("{");
            appendField(sb, "accountId", a.getAccountId(), true);
            appendField(sb, "accountNumber", a.getAccountNumber(), false);
            appendField(sb, "ownerName", a.getOwnerName(), false);
            appendField(sb, "balance", a.getBalance() != null ? a.getBalance().toPlainString() : "0", false);
            appendField(sb, "status", a.getStatus(), false);
            sb.append("}");
            return sb.toString();
        }

        if (obj instanceof Transaction) {
            Transaction t = (Transaction) obj;
            StringBuilder sb = new StringBuilder("{");
            appendField(sb, "transactionId", t.getTransactionId(), true);
            appendField(sb, "sourceAccountId", t.getSourceAccountId(), false);
            appendField(sb, "targetAccountId", t.getTargetAccountId(), false);
            appendField(sb, "transactionType", t.getTransactionType(), false);
            appendField(sb, "amount", t.getAmount() != null ? t.getAmount().toPlainString() : "0", false);
            appendField(sb, "status", t.getStatus(), false);
            appendField(sb, "description", t.getDescription(), false);
            sb.append(",\"timestamp\":").append(t.getTimestamp());
            sb.append("}");
            return sb.toString();
        }

        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private static void appendField(StringBuilder sb, String key, String val, boolean first) {
        if (!first) {
            sb.append(",");
        }
        sb.append("\"").append(key).append("\":");
        if (val == null) {
            sb.append("null");
        } else {
            sb.append("\"").append(escapeJson(val)).append("\"");
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        Map<String, String> map = parseJsonToMap(json);

        if (clazz.equals(Request.class)) {
            Request r = new Request();
            r.setRequestId(map.get("requestId"));
            r.setMessageType(MessageType.fromString(map.get("messageType")));
            r.setCommand(Command.fromString(map.get("command")));
            r.setAccountId(map.get("accountId"));
            r.setTargetAccountId(map.get("targetAccountId"));
            if (map.get("amount") != null) {
                try {
                    r.setAmount(new BigDecimal(map.get("amount")));
                } catch (Exception ignored) {}
            }
            r.setUsername(map.get("username"));
            r.setPassword(map.get("password"));
            r.setPayload(map.get("payload"));
            if (map.get("timestamp") != null) {
                try {
                    r.setTimestamp(Long.parseLong(map.get("timestamp")));
                } catch (Exception ignored) {}
            }
            return clazz.cast(r);
        }

        if (clazz.equals(Response.class)) {
            Response r = new Response();
            r.setRequestId(map.get("requestId"));
            r.setMessageType(MessageType.fromString(map.get("messageType")));
            r.setStatus(Status.fromString(map.get("status")));
            r.setMessage(map.get("message"));
            r.setData(map.get("data"));
            r.setServerNodeId(map.get("serverNodeId"));
            if (map.get("timestamp") != null) {
                try {
                    r.setTimestamp(Long.parseLong(map.get("timestamp")));
                } catch (Exception ignored) {}
            }
            return clazz.cast(r);
        }

        if (clazz.equals(Account.class)) {
            Account a = new Account();
            a.setAccountId(map.get("accountId"));
            a.setAccountNumber(map.get("accountNumber"));
            a.setOwnerName(map.get("ownerName"));
            if (map.get("balance") != null) {
                try {
                    a.setBalance(new BigDecimal(map.get("balance")));
                } catch (Exception ignored) {}
            }
            a.setStatus(map.get("status"));
            return clazz.cast(a);
        }

        if (clazz.equals(Transaction.class)) {
            Transaction t = new Transaction();
            t.setTransactionId(map.get("transactionId"));
            t.setSourceAccountId(map.get("sourceAccountId"));
            t.setTargetAccountId(map.get("targetAccountId"));
            t.setTransactionType(map.get("transactionType"));
            if (map.get("amount") != null) {
                try {
                    t.setAmount(new BigDecimal(map.get("amount")));
                } catch (Exception ignored) {}
            }
            t.setStatus(map.get("status"));
            t.setDescription(map.get("description"));
            if (map.get("timestamp") != null) {
                try {
                    t.setTimestamp(Long.parseLong(map.get("timestamp")));
                } catch (Exception ignored) {}
            }
            return clazz.cast(t);
        }

        return null;
    }

    public static Map<String, String> parseJsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        String trimmed = json.trim();
        if (trimmed.startsWith("{")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("}")) trimmed = trimmed.substring(0, trimmed.length() - 1);

        boolean inQuotes = false;
        boolean escape = false;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean parsingKey = true;

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);

            if (escape) {
                if (parsingKey) currentKey.append(c);
                else currentValue.append(c);
                escape = false;
                continue;
            }

            if (c == '\\') {
                escape = true;
                continue;
            }

            if (c == '\"') {
                inQuotes = !inQuotes;
                continue;
            }

            if (!inQuotes) {
                if (c == ':') {
                    parsingKey = false;
                    continue;
                } else if (c == ',') {
                    storeEntry(map, currentKey, currentValue);
                    currentKey.setLength(0);
                    currentValue.setLength(0);
                    parsingKey = true;
                    continue;
                }
            }

            if (parsingKey) {
                if (!Character.isWhitespace(c) || inQuotes) {
                    currentKey.append(c);
                }
            } else {
                currentValue.append(c);
            }
        }

        storeEntry(map, currentKey, currentValue);
        return map;
    }

    private static void storeEntry(Map<String, String> map, StringBuilder keySb, StringBuilder valSb) {
        String key = keySb.toString().trim();
        String val = valSb.toString().trim();
        if (!key.isEmpty()) {
            if ("null".equalsIgnoreCase(val)) {
                map.put(key, null);
            } else {
                map.put(key, unescapeJson(val));
            }
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String s) {
        if (s == null) return null;
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}
