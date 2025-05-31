package net.apartium.cocoabeans.state.animation;

import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

@ApiStatus.AvailableSince("0.0.39")
public class FadingColorBlinkObservable implements Observable<Component>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private final Observable<String> textObservable;
    private final Observable<Instant> nowObservable;

    private final long stayAtStart;
    private final long characterDelay;
    private final int blinkTimes;
    private final long blinkLength;

    private final Style in;
    private final Style fade;
    private final Style out;

    private final Observable<Long> textAnimationLength;
    private final Observable<Long> animationLength;

    private boolean dirty = true;
    private Component cacheText = null;
    private String lastText = null;
    private long lastTick = -1;

    public FadingColorBlinkObservable(Observable<String> textObservable, Observable<Instant> nowObservable, Duration stayAtStart, Duration characterDelay, int blinkTimes, Duration blinkLength, Style in, Style fade, Style out) {
        this.textObservable = textObservable;
        this.nowObservable = nowObservable;

        this.stayAtStart = stayAtStart.toMillis();
        this.characterDelay = characterDelay.toMillis();
        this.blinkLength = blinkLength.toMillis();
        this.blinkTimes = blinkTimes;

        this.in = in;
        this.fade = fade;
        this.out = out;

        this.textAnimationLength = textObservable.map(text -> text.length() * this.characterDelay);
        this.animationLength = textAnimationLength.map(textLength -> textLength + this.stayAtStart + (long) this.blinkTimes * this.blinkLength);

        // Observers
        this.textObservable.observe(this);
        this.nowObservable.observe(this);
    }

    @Override
    public Component get() {
        if (!dirty)
            return cacheText;

        long animationLength = this.animationLength.get();
        long currentTick = nowObservable.get().toEpochMilli() % animationLength;
        String text = textObservable.get();

        if (currentTick == lastTick && Objects.equals(lastText, text))
            return cacheText;

        dirty = false;
        lastTick = currentTick;
        lastText = text;

        long textAnimationLength = this.textAnimationLength.get();
        if (currentTick <= stayAtStart)
            cacheText = Component.text(text)
                    .style(in);
        else if (textAnimationLength + stayAtStart <= currentTick)
            cacheText = Component.text(text)
                    .style(((currentTick - textAnimationLength - stayAtStart) / blinkLength) % 2 == 0
                            ? out
                            : in
                    );
        else {
            int index = (int) ((currentTick - stayAtStart) / characterDelay);
            cacheText = fadeByIndex(text, index);
        }

        return cacheText;
    }

    private Component fadeByIndex(String text, int index) {
        return getComponent(text, index, fade, in, out);

    }

    @NotNull
    static Component getComponent(String text, int index, Style fade, Style in, Style out) {
        if (index == 0)
            return Component.text(text.charAt(0))
                    .style(fade)
                    .append(Component.text(text.substring(1))
                            .style(in)
                    );

        if (index == text.length() - 1)
            return Component.text(text.substring(0, text.length() - 1))
                    .style(out)
                    .append(Component.text(text.substring(text.length() - 1))
                            .style(fade)
                    );

        return Component.text(text.substring(0, index))
                .style(out)
                .append(Component.text(text.charAt(index))
                        .style(fade)
                        .append(Component.text(text.substring(index + 1))
                                .style(in)
                        )
                );
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (textObservable != observable && nowObservable != observable)
            return;

        dirty = true;

        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

}
