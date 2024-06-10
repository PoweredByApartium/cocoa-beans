package net.apartium.cocoabeans.utils;

public interface TimeProvider {

    static TimeProvider create() {
        return System::currentTimeMillis;
    }

    long getTime();

}
