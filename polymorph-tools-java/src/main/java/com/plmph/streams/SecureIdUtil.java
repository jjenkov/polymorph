package com.plmph.streams;

import java.security.SecureRandom;

public class SecureIdUtil {

    public static final int STREAM_ID_LENGTH_BYTES = 32;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SecureIdUtil() {}

    public static byte[] generateSecureId() {
        byte[] id = new byte[STREAM_ID_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(id);
        return id;
    }


}
