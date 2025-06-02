package net.apartium.cocoabeans.state.animation;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * An animation implementation based on the state api.
 * See the relevant docs for a demonstration.
 */
@ApiStatus.AvailableSince("0.0.39")
public class FixedDoubleLerpObservable implements Observable<Double>, Observer {

    private final Observable<Double> source;
    private final Observable<Instant> nowObservable;
    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());
    private final Duration duration;
    private Instant startInstant = null;


    private double self;
    private double last;
    private double start;

    private boolean dirty = false;

    /**
     * Constructs a new instance of this class
     * @param source source number
     * @param nowObservable current time observable
     * @param duration duration of the animation
     */
    public FixedDoubleLerpObservable(Observable<Double> source, Observable<Instant> nowObservable, Duration duration) {
        this.source = source;
        this.nowObservable = nowObservable;
        last = this.source.get();
        start = last;
        self = last;

        this.duration = duration;
        this.source.observe(this);
        this.nowObservable.observe(this);
    }


    /**
     * @inheritDoc
     */
    @Override
    public Double get() {
        double current = source.get();

        if (start != current) {
            startInstant = nowObservable.get();
            dirty = true;
            start = current;
            last = self;
        }

        if (current == self) {
            dirty = false;
            last = self;
            return self;
        }

        dirty = true;

        self = Mathf.lerp(last, current, Math.min(1.0, Math.max(0.0, (double) Duration.between(startInstant, nowObservable.get()).toMillis() / duration.toMillis())));

        return self;
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
        if (dirty && nowObservable == observable) {
            for (Observer observer : observers)
                observer.flagAsDirty(this);
            return;
        }

        if (source != observable)
            return;

        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }
}