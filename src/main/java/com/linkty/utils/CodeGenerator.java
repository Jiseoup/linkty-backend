package com.linkty.utils;

import java.security.SecureRandom;

public class CodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHANUMERIC_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC_CHARSET = "0123456789";

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
            stringBuilder.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return stringBuilder.toString();
    }
}
