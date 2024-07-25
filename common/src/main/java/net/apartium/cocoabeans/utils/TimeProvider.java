package net.apartium.cocoabeans.utils;

import org.jetbrains.annotations.ApiStatus;

import java.time.Clock;

@FunctionalInterface
@ApiStatus.AvailableSince("0.0.25")
public interface TimeProvider {

    /**
     * Create a TimeProvider using the given clock
     * @param clock clock instance
     * @return TimeProvider instance
     * @see Clock
     */
    static TimeProvider create(Clock clock) {
        return clock::millis;
    }

    /**
     * Create a TimeProvider using the system time
     * @return TimeProvider instance
     */
    static TimeProvider create() {
        return System::currentTimeMillis;
    }

    /**
     * Get the current time in milliseconds
     * @return current time in milliseconds
     */
    long getTime();

}
