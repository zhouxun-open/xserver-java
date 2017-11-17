package xserver.util;

import java.security.MessageDigest;

public final class SHA256Util {
    /**
     * MD5字符
     */
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    public static String encode(final String password) throws Exception {
        if (password == null) {
            return null;
        }
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(password.getBytes());
        final byte[] digest = messageDigest.digest();
        return SHA256Util.getFormattedText(digest);
    }

    private static String getFormattedText(byte[] bytes) {
        final StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (int j = 0; j < bytes.length; j++) {
            buf.append(SHA256Util.HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(SHA256Util.HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
}
