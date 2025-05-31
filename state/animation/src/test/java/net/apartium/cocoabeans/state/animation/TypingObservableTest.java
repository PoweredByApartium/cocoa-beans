package net.apartium.cocoabeans.state.animation;

import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypingObservableTest {

    @Test
    void test() {
        MutableObservable<Instant> now = Observable.mutable(Instant.ofEpochMilli(0L));
        MutableObservable<String> text = Observable.mutable("Hello, World!");

        TypingObservable typingObservable = new TypingObservable(
                text,
                now,
                Duration.ofMillis(1250),
                Duration.ofMillis(10_000),
                Duration.ofMillis(100),
                "§k"
        );

        assertEquals("", typingObservable.get());
        assertEquals("", typingObservable.get());

        now.set(Instant.ofEpochMilli(70));
        assertEquals("", typingObservable.get());

        now.set(Instant.ofEpochMilli(1249));
        assertEquals("", typingObservable.get());
        now.set(Instant.ofEpochMilli(1250));
        assertEquals("", typingObservable.get());
        now.set(Instant.ofEpochMilli(1251));
        assertEquals("§kH", typingObservable.get());
        now.set(Instant.ofEpochMilli(1350));
        assertEquals("H§ke", typingObservable.get());
        now.set(Instant.ofEpochMilli(1450));
        assertEquals("He§kl", typingObservable.get());
        now.set(Instant.ofEpochMilli(1550));
        assertEquals("Hel§kl", typingObservable.get());

        now.set(Instant.ofEpochMilli(1559));
        now.set(Instant.ofEpochMilli(1550));
        assertEquals("Hel§kl", typingObservable.get());

        now.set(Instant.ofEpochMilli(1650));
        assertEquals("Hell§ko", typingObservable.get());
        now.set(Instant.ofEpochMilli(1750));
        assertEquals("Hello§k,", typingObservable.get());
        now.set(Instant.ofEpochMilli(1850));
        assertEquals("Hello,§k ", typingObservable.get());
        now.set(Instant.ofEpochMilli(1950));
        assertEquals("Hello, §kW", typingObservable.get());
        now.set(Instant.ofEpochMilli(2050));
        assertEquals("Hello, W§ko", typingObservable.get());
        now.set(Instant.ofEpochMilli(2150));
        assertEquals("Hello, Wo§kr", typingObservable.get());
        now.set(Instant.ofEpochMilli(2250));
        assertEquals("Hello, Wor§kl", typingObservable.get());
        now.set(Instant.ofEpochMilli(2350));
        assertEquals("Hello, Worl§kd", typingObservable.get());
        now.set(Instant.ofEpochMilli(2450));
        assertEquals("Hello, World§k!", typingObservable.get());
        now.set(Instant.ofEpochMilli(2550));
        assertEquals("Hello, World!", typingObservable.get());

        now.set(Instant.ofEpochMilli(12550));
        assertEquals("Hello, World!", typingObservable.get());
        now.set(Instant.ofEpochMilli(12551));
        assertEquals("Hello, World§k!", typingObservable.get());
        now.set(Instant.ofEpochMilli(12650));
        assertEquals("Hello, Worl§kd", typingObservable.get());
        now.set(Instant.ofEpochMilli(12750));
        assertEquals("Hello, Wor§kl", typingObservable.get());
        now.set(Instant.ofEpochMilli(12850));
        assertEquals("Hello, Wo§kr", typingObservable.get());
        now.set(Instant.ofEpochMilli(12950));
        assertEquals("Hello, W§ko", typingObservable.get());
        now.set(Instant.ofEpochMilli(13050));
        assertEquals("Hello, §kW", typingObservable.get());
        now.set(Instant.ofEpochMilli(13150));
        assertEquals("Hello,§k ", typingObservable.get());
        now.set(Instant.ofEpochMilli(13250));
        assertEquals("Hello§k,", typingObservable.get());
        now.set(Instant.ofEpochMilli(13350));
        assertEquals("Hell§ko", typingObservable.get());
        now.set(Instant.ofEpochMilli(13450));
        assertEquals("Hel§kl", typingObservable.get());
        now.set(Instant.ofEpochMilli(13550));
        assertEquals("He§kl", typingObservable.get());
        now.set(Instant.ofEpochMilli(13650));
        assertEquals("H§ke", typingObservable.get());
        now.set(Instant.ofEpochMilli(13750));
        assertEquals("§kH", typingObservable.get());
        now.set(Instant.ofEpochMilli(13950));
        assertEquals("", typingObservable.get());
        now.set(Instant.ofEpochMilli(14050));
        assertEquals("", typingObservable.get());

        now.set(Instant.ofEpochMilli(1251));
        assertEquals("§kH", typingObservable.get());

        text.set("Xd");
        assertEquals("§kX", typingObservable.get());

        now.set(Instant.ofEpochMilli(1252));
        now.set(Instant.ofEpochMilli(1251));
        text.set("lol");
        assertEquals("§kl", typingObservable.get());

        Observable<String> map = typingObservable.map(t -> t);
        assertEquals("§kl", map.get());
        now.set(Instant.ofEpochMilli(1351));
        assertEquals("l§ko", map.get());

        typingObservable.flagAsDirty(map);
        typingObservable.removeObserver((Observer) map);
    }

}
