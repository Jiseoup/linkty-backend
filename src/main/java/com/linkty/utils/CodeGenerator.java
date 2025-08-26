package com.linkty.utils;

import java.util.Base64;
import java.security.SecureRandom;
import java.security.MessageDigest;

import com.linkty.exception.CustomException;
import com.linkty.exception.ErrorCode;

public class CodeGenerator {

    private static final String ALPHANUMERIC_CHARSET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC_CHARSET = "0123456789";

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder base64Encoder =
            Base64.getUrlEncoder().withoutPadding();

    // Generates a random alphanumeric code of a given length.
    public static String generateAlphanumeric(int length) {
        return generateCode(ALPHANUMERIC_CHARSET, length);
    }

    // Generates a random numeric code of a given length.
    public static String generateNumeric(int length) {
        return generateCode(NUMERIC_CHARSET, length);
    }

    private static String generateCode(String charSet, int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder
                    .append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return stringBuilder.toString();
    }

    // Generates a random token with 256-bit.
    public static String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }

    // Generates a hash token with SHA-256 for safe storage.
    public static String generateHashToken(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(token.getBytes());
            return base64Encoder.encodeToString(hashBytes);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
