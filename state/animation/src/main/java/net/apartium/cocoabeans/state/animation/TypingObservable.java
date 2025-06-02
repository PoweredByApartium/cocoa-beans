package net.apartium.cocoabeans.state.animation;

import net.apartium.cocoabeans.state.*;
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
public class TypingObservable implements Observable<String>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());
    private final Observable<String> textObservable;
    private final Observable<Long> animationLength;
    private final Observable<Long> textAnimationLength;
    private final Observable<Instant> nowObservable;

    private boolean dirty = true;
    private String cacheText = null;
    private String lastText = null;
    private long lastTick = -1;

    private final long stayAtStart;
    private final long stayAtEnd;

    private final long writingSpeedPerCharacter;

    private final String prefix;

    /**
     * Constructs a new instance of this class
     * @param textObservable source number
     * @param nowObservable current time observable
     * @param stayAtStart time to start at start of cycle
     * @param stayAtEnd time to start at end of cycle
     * @param writingSpeedPerCharacter writing speed per character
     * @param prefix add before each new character
     */
    public TypingObservable(Observable<String> textObservable, Observable<Instant> nowObservable, Duration stayAtStart, Duration stayAtEnd, Duration writingSpeedPerCharacter, String prefix) {
        this.textObservable = textObservable;
        this.nowObservable = nowObservable;

        this.stayAtStart = stayAtStart.toMillis();
        this.stayAtEnd = stayAtEnd.toMillis();
        this.writingSpeedPerCharacter = writingSpeedPerCharacter.toMillis();

        this.prefix = prefix;

        this.textAnimationLength = textObservable.map(text -> this.writingSpeedPerCharacter * text.length());
        this.animationLength = textAnimationLength.map(textAnimationLength -> textAnimationLength * 2 + this.stayAtStart + this.stayAtEnd);

        // Observes
        this.textObservable.observe(this);
        this.nowObservable.observe(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String get() {
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

        if (currentTick <= stayAtStart) {
            cacheText = "";
        } else if (textAnimationLength + stayAtStart <= currentTick && currentTick <= textAnimationLength + stayAtStart + stayAtEnd) {
            cacheText = text;
        } else if (currentTick >= stayAtStart + stayAtEnd + textAnimationLength) {
            cacheText = typeByIndex(
                    text,
                    (int) (text.length() - 1 - (currentTick - stayAtStart - stayAtEnd - textAnimationLength) / writingSpeedPerCharacter)
            );
        } else {
            cacheText = typeByIndex(
                    text,
                    (int) ((currentTick - stayAtStart) / writingSpeedPerCharacter)
            );
        }

        return cacheText;
    }

    private String typeByIndex(String text, int index) {
        if (index == 0)
            return prefix + text.charAt(0);

        if (index == text.length() - 1)
            return text.substring(0, text.length() - 1) + prefix + text.charAt(text.length() - 1);

        return text.substring(0, index) + prefix + text.charAt(index);
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
