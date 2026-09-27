package util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {}



    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }



    public static boolean checkPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }


        if (isBCryptHash(storedPassword)) {
            try {
                return BCrypt.checkpw(plainPassword, storedPassword);
            } catch (Exception e) {
                System.err.println("Lỗi kiểm tra BCrypt hash: " + e.getMessage());
                return false;
            }
        }


        return plainPassword.equals(storedPassword);
    }



    public static boolean isBCryptHash(String password) {
        if (password == null || password.length() < 59) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }
}
