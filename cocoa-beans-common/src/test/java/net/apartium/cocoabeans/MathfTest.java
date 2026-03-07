package net.apartium.cocoabeans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathfTest {

    @Test
    void lerpInt() {
        assertEquals(0, Mathf.lerp(0, 100, 0.f));
        assertEquals(25, Mathf.lerp(0, 100, 0.25f));
        assertEquals(50, Mathf.lerp(0, 100, 0.5f));
        assertEquals(75, Mathf.lerp(0, 100, 0.75f));
        assertEquals(100, Mathf.lerp(0, 100, 1f));
    }

    @Test
    void lerpLong() {
        assertEquals(0L, Mathf.lerp(0L, 100L, 0.f));
        assertEquals(25L, Mathf.lerp(0L, 100L, 0.25f));
        assertEquals(50L, Mathf.lerp(0L, 100L, 0.5f));
        assertEquals(75L, Mathf.lerp(0L, 100L, 0.75f));
        assertEquals(100L, Mathf.lerp(0L, 100L, 1f));
    }

    @Test
    void lerpFloat() {
        assertEquals(0f, Mathf.lerp(0f, 100f, 0.f));
        assertEquals(25f, Mathf.lerp(0f, 100f, 0.25f));
        assertEquals(50f, Mathf.lerp(0f, 100f, 0.5f));
        assertEquals(75f, Mathf.lerp(0f, 100f, 0.75f));
        assertEquals(100f, Mathf.lerp(0f, 100f, 1f));
    }

    @Test
    void lerpDouble() {
        assertEquals(0.0, Mathf.lerp(0.0, 100.0, 0.0));
        assertEquals(25.0, Mathf.lerp(0.0, 100.0, 0.25));
        assertEquals(50.0, Mathf.lerp(0.0, 100.0, 0.5));
        assertEquals(75.0, Mathf.lerp(0.0, 100.0, 0.75));
        assertEquals(100.0, Mathf.lerp(0.0, 100.0, 1));
    }

    @Test
    void nextPowerOfFourTest() {
        // Edge cases
        assertEquals(1L, Mathf.nextPowerOfFour(0));
        assertEquals(1L, Mathf.nextPowerOfFour(1));
        // Powers of four
        assertEquals(4L, Mathf.nextPowerOfFour(2));
        assertEquals(4L, Mathf.nextPowerOfFour(3));
        assertEquals(4L, Mathf.nextPowerOfFour(4));
        assertEquals(16L, Mathf.nextPowerOfFour(5));
        assertEquals(16L, Mathf.nextPowerOfFour(16));
        assertEquals(64L, Mathf.nextPowerOfFour(17));
    }

    @Test
    void log4Test() {
        assertEquals(0.0, Mathf.log4(1L), 1e-9);
        assertEquals(1.0, Mathf.log4(4L), 1e-9);
        assertEquals(2.0, Mathf.log4(16L), 1e-9);
        assertEquals(3.0, Mathf.log4(64L), 1e-9);
        assertEquals(4.0, Mathf.log4(256L), 1e-9);
        // Non-exact power of four
        double val = Mathf.log4(10L);
        assertEquals(Math.log(10L) / Math.log(4), val, 1e-9);
    }

}
