package com.urlshortener.util;

/**
 * Utility class for Base62 encoding and decoding.
 * Uses the alphabet 0-9a-zA-Z (62 characters).
 */
public final class Base62Encoder {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    private Base62Encoder() {
        // Prevent instantiation of utility class
    }

    /**
     * Encodes a non-negative long value into a Base62 string.
     *
     * @param value non-negative long to encode
     * @return Base62 encoded string
     * @throws IllegalArgumentException if value is negative
     */
    public static String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative: " + value);
        }
        if (value == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % BASE);
            sb.append(ALPHABET.charAt(remainder));
            value /= BASE;
        }

        return sb.reverse().toString();
    }

    /**
     * Decodes a Base62 string into a non-negative long value.
     *
     * @param str Base62 string to decode
     * @return decoded long value
     * @throws IllegalArgumentException if str is null, empty, contains invalid characters,
     *                                  or exceeds Long.MAX_VALUE
     */
    public static long decode(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        long result = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int digit = charToValue(c);

            // Check for overflow before multiplying and adding
            if (result > (Long.MAX_VALUE - digit) / BASE) {
                throw new IllegalArgumentException("Base62 string exceeds Long.MAX_VALUE: " + str);
            }

            result = result * BASE + digit;
        }

        return result;
    }

    private static int charToValue(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'z') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 36;
        }
        throw new IllegalArgumentException("Invalid character in Base62 string: '" + c + "'");
    }
}
