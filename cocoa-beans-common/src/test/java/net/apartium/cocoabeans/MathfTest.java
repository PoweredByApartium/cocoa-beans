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

}
