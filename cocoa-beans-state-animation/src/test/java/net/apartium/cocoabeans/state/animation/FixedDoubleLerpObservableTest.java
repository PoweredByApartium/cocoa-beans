package net.apartium.cocoabeans.state.animation;


import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedDoubleLerpObservableTest {

    @Test
    void test() {
        MutableObservable<Double> source = Observable.mutable(0.0);
        MutableObservable<Instant> now = Observable.mutable(Instant.ofEpochMilli(0L));

        Duration tickRate = Duration.ofMillis(50);
        Duration duration = Duration.ofMillis(1000);

        FixedDoubleLerpObservable lerpObservable = new FixedDoubleLerpObservable(
                source,
                now,
                duration
        );

        assertEquals(0, lerpObservable.get());
        source.set(100.0);
        assertEquals(0, lerpObservable.get());

        for (int i = 1; i <= 20; i++) {
            now.set(Instant.ofEpochMilli(tickRate.toMillis() * i));
            double millis = Duration.between(Instant.ofEpochMilli(0), now.get()).toMillis();
            assertEquals(Mathf.lerp(0, 100, Math.min(1.0, Math.max(0.0, millis / duration.toMillis()))), lerpObservable.get());
        }

        Observable<Double> map = lerpObservable.map(d -> d);
        now.set(Instant.ofEpochMilli(0));
        source.set(0.0);
        assertEquals(Mathf.lerp(100, 0, 0), map.get());
        now.set(Instant.ofEpochMilli(50));
        assertEquals(Mathf.lerp(100, 0, 0.05), map.get());
        now.set(Instant.ofEpochMilli(56));
        assertEquals(Mathf.lerp(100, 0, 0.056), map.get());

        for (int i = 0; i <= 1000; i++) {
            now.set(Instant.ofEpochMilli(i));
            assertEquals(Mathf.lerp(100, 0, i / 1000.0), map.get());

        }

        lerpObservable.flagAsDirty(map);

        lerpObservable.removeObserver((Observer) map);
    }

}
