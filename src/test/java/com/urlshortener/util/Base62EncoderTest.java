package com.urlshortener.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    @Test
    @DisplayName("Encode and decode zero")
    void testZero() {
        String encoded = Base62Encoder.encode(0L);
        assertEquals("0", encoded);
        assertEquals(0L, Base62Encoder.decode(encoded));
    }

    @Test
    @DisplayName("Encode and decode 1000")
    void testOneThousand() {
        // 1000 = 16 * 62 + 8 => alphabet[16]='g', alphabet[8]='8' => "g8"
        String encoded = Base62Encoder.encode(1000L);
        assertEquals("g8", encoded);
        assertEquals(1000L, Base62Encoder.decode("g8"));
    }

    @Test
    @DisplayName("Encode and decode Long.MAX_VALUE")
    void testLongMaxValue() {
        String encoded = Base62Encoder.encode(Long.MAX_VALUE);
        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());
        assertEquals(Long.MAX_VALUE, Base62Encoder.decode(encoded));
    }

    @Test
    @DisplayName("Round-trip on boundary values, powers of 2, and 62")
    void testRoundTripBoundaries() {
        long[] testValues = {
                0L, 1L, 9L, 10L, 35L, 36L, 61L, 62L, 63L,
                1000L, 3843L, 3844L, 238327L, 238328L,
                (long) Integer.MAX_VALUE,
                (long) Integer.MAX_VALUE + 1L,
                1L << 32,
                1L << 40,
                1L << 50,
                1L << 60,
                1L << 62,
                Long.MAX_VALUE - 1L,
                Long.MAX_VALUE
        };

        for (long value : testValues) {
            String encoded = Base62Encoder.encode(value);
            long decoded = Base62Encoder.decode(encoded);
            assertEquals(value, decoded, "Mismatch for value: " + value + " (encoded: " + encoded + ")");
        }
    }

    @Test
    @DisplayName("Round-trip on 10,000 sequential values and random values")
    void testRoundTripManyValues() {
        // Sequential values 0 to 10,000
        for (long i = 0; i <= 10000; i++) {
            assertEquals(i, Base62Encoder.decode(Base62Encoder.encode(i)));
        }

        // 10,000 random non-negative longs
        Random random = new Random(42);
        for (int i = 0; i < 10000; i++) {
            long val = random.nextLong() & Long.MAX_VALUE; // ensure non-negative
            assertEquals(val, Base62Encoder.decode(Base62Encoder.encode(val)));
        }
    }

    @Test
    @DisplayName("Encode negative numbers throws IllegalArgumentException")
    void testNegativeNumbers() {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.encode(-1L));
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.encode(-1000L));
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.encode(Long.MIN_VALUE));
    }

    @Test
    @DisplayName("Decode null or empty string throws IllegalArgumentException")
    void testNullOrEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.decode(null));
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.decode(""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc-123", "hello!", "123@45", "a b", "test_id", "xyz#", "123.4", "🚀", "ä"})
    @DisplayName("Decode strings with invalid characters throws IllegalArgumentException")
    void testInvalidCharacters(String invalidInput) {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.decode(invalidInput));
    }

    @Test
    @DisplayName("Decode string exceeding Long.MAX_VALUE throws IllegalArgumentException")
    void testOverflow() {
        // "zzzzzzzzzzz" or larger exceeds Long.MAX_VALUE
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.decode("zzzzzzzzzzzz"));
    }
}
