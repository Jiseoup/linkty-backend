package com.linkty.utils;

import com.linkty.email.EmailPurposeEnum;

public class KeyGenerator {
    // Generates redis email verification key.
    public static String generateEmailVerificationKey(String email,
            EmailPurposeEnum purpose) {
        return String.format("%s:%s", email, purpose.name());
    }
}
