package net.apartium.cocoabeans.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeProviderTest {

    @Test
    void currentTime() {
        TimeProvider timeProvider = TimeProvider.create();

        long before = System.currentTimeMillis();
        long time = timeProvider.getTime();
        long after = System.currentTimeMillis();

        assertTrue(time >= before);
        assertTrue(time <= after);
    }

    @Test
    void fakeTime() {
        TimeProvider timeProvider = () -> 1000L;

        assertEquals(1000L, timeProvider.getTime());
        assertEquals(1000L, timeProvider.getTime());
    }

}
