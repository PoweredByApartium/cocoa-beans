package net.apartium.cocoabeans.state.animation;

import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FadingColorBlinkObservableTest {

    @Test
    void test() {
        MutableObservable<Instant> now = Observable.mutable(Instant.ofEpochMilli(0L));

        Style in = Style.style(NamedTextColor.YELLOW);
        Style fade = Style.style(NamedTextColor.GOLD);
        Style out = Style.style(NamedTextColor.WHITE);

        MutableObservable<String> text = Observable.mutable("test");
        FadingColorBlinkObservable fadingColor = new FadingColorBlinkObservable(
                text,
                now,
                Duration.ofMillis(3500),
                Duration.ofMillis(100),
                3,
                Duration.ofMillis(250),
                in,
                fade,
                out
        );

        assertEquals(Component.text("test").style(in), fadingColor.get());
        now.set(Instant.ofEpochMilli(3500));
        assertEquals(Component.text("test").style(in), fadingColor.get());

        now.set(Instant.ofEpochMilli(3501));
        assertEquals(Component.text("t")
                .style(fade)
                .append(Component.text("est")
                        .style(in)
                ), fadingColor.get());

        now.set(Instant.ofEpochMilli(3600));
        assertEquals(Component.text("t")
                .style(out)
                .append(Component.text("e")
                        .style(fade)
                        .append(Component.text("st")
                                .style(in))
                ), fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(3700));
        assertEquals(Component.text("te")
                .style(out)
                .append(Component.text("s")
                        .style(fade)
                        .append(Component.text("t")
                                .style(in))
                ), fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(3800));
        assertEquals(Component.text("tes")
                .style(out)
                .append(Component.text("t")
                        .style(fade)
                ), fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(3900));
        assertEquals(
                Component.text("test")
                        .style(out),
                fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(3901));
        assertEquals(
                Component.text("test")
                        .style(out),
                fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(4150));
        assertEquals(
                Component.text("test")
                        .style(in),
                fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(4400));
        assertEquals(
                Component.text("test")
                        .style(out),
                fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(4650));
        assertEquals(
                Component.text("test")
                        .style(in),
                fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(5000));
        assertEquals(
                Component.text("test")
                        .style(in),
                fadingColor.get()
        );

        now.set(Instant.ofEpochMilli(5000));
        assertEquals(
                Component.text("test")
                        .style(in),
                fadingColor.get()
        );

        text.set("wow");
        text.set("test");

        Observable<Component> map = fadingColor.map(t -> t);
        assertEquals(
                Component.text("test")
                        .style(in),
                fadingColor.get()
        );

        assertEquals(fadingColor.get(), map.get());

        text.set("cool");
        assertEquals(
                Component.text("cool")
                        .style(in),
                fadingColor.get()
        );
        assertEquals(fadingColor.get(), map.get());

        fadingColor.flagAsDirty(map);
        fadingColor.removeObserver((Observer) map);
    }
}
