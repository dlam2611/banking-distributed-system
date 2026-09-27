package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords securely using BCrypt.
 * Includes backward compatibility for existing plain text passwords.
 */
public final class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {}

    /**
     * Băm mật khẩu bằng thuật toán BCrypt với salt ngẫu nhiên (work factor = 12).
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Kiểm tra mật khẩu người dùng nhập vào so với mật khẩu đã lưu trong database.
     * Hỗ trợ tự động nhận diện cả mật khẩu đã băm BCrypt và mật khẩu chưa băm (legacy).
     */
    public static boolean checkPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }

        // Nếu đã được băm BCrypt ($2a$, $2b$, hoặc $2y$)
        if (isBCryptHash(storedPassword)) {
            try {
                return BCrypt.checkpw(plainPassword, storedPassword);
            } catch (Exception e) {
                System.err.println("Lỗi kiểm tra BCrypt hash: " + e.getMessage());
                return false;
            }
        }

        // Tương thích ngược: Nếu database còn lưu mật khẩu dạng thường
        return plainPassword.equals(storedPassword);
    }

    /**
     * Kiểm tra xem mật khẩu có phải dạng BCrypt hash hợp lệ hay không.
     */
    public static boolean isBCryptHash(String password) {
        if (password == null || password.length() < 59) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
