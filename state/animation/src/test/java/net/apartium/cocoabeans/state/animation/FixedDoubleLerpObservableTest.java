package net.apartium.cocoabeans.state.animation;


import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedDoubleLerpObservableTest {

    @Test
    void test() {
        MutableObservable<Double> source = Observable.mutable(0.0);
        MutableObservable<Instant> now = Observable.mutable(Instant.ofEpochMilli(0L));

        FixedDoubleLerpObservable lerpObservable = new FixedDoubleLerpObservable(
                source,
                now,
                Duration.ofMillis(1000),
                Duration.ofMillis(50)
        );

        assertEquals(0, lerpObservable.get());
        source.set(100.0);
        assertEquals(0, lerpObservable.get());

        for (int i = 1; i <= 20; i++) {
            now.set(Instant.ofEpochMilli(50 * i));
            assertEquals(Mathf.lerp(0, 100, i * 0.05), lerpObservable.get());
        }

        Observable<Double> map = lerpObservable.map(d -> d);
        now.set(Instant.ofEpochMilli(0));
        source.set(0.0);
        assertEquals(Mathf.lerp(100, 0, 0), map.get());
        now.set(Instant.ofEpochMilli(50));
        assertEquals(Mathf.lerp(100, 0, 0.05), map.get());

        lerpObservable.flagAsDirty(map);

        lerpObservable.removeObserver((Observer) map);
    }

}
