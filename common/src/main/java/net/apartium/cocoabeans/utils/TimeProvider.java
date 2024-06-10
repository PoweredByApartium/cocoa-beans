package net.apartium.cocoabeans.utils;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.24")
public interface TimeProvider {

    static TimeProvider create() {
        return System::currentTimeMillis;
    }

    long getTime();

}
