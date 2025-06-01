package net.apartium.cocoabeans.state.animation;

import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * An animation implementation based on the state api.
 * See the relevant docs for a demonstration.
 */
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

    /**
     * Constructs a new instance of this class
     * @param textObservable original text
     * @param nowObservable current time observable
     * @param stayAtStart time to start at start of cycle
     * @param characterDelay delay of transition between each character
     * @param blinkTimes how many times the text will blink
     * @param blinkLength how long the text will blink for
     * @param in text style for in
     * @param fade text style for fade
     * @param out text style for out
     */
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
        this.animationLength = textAnimationLength.map(textLength -> textLength + this.stayAtStart + this.blinkTimes * this.blinkLength);

        // Observers
        this.textObservable.observe(this);
        this.nowObservable.observe(this);
    }

    /**
     * @inheritDoc
     */
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
        return AnimationHelpers.fading(text, index, fade, in, out);

    }

    /**
     * @inheritDoc
     */
    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (textObservable != observable && nowObservable != observable)
            return;

        dirty = true;

        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

}
