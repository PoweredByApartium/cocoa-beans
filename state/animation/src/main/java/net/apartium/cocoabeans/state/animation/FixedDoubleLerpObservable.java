package net.apartium.cocoabeans.state.animation;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class FixedDoubleLerpObservable implements Observable<Double>, Observer {

    private final Observable<Double> source;
    private final Observable<Instant> nowObservable;
    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());
    private final double t;
    private int multiplier = 0;


    private double self;
    private double last;
    private double start;

    private boolean dirty = false;

    public FixedDoubleLerpObservable(Observable<Double> source, Observable<Instant> nowObservable, Duration duration, Duration tickRate) {
        this.source = source;
        this.nowObservable = nowObservable;
        last = this.source.get();
        start = last;
        self = last;

        int totalJumps = (int) (duration.toMillis() / tickRate.toMillis());

        this.t = 1.0 / totalJumps;

        this.source.observe(this);
        this.nowObservable.observe(this);
    }


    @Override
    public Double get() {
        double current = source.get();

        if (start != current) {
            multiplier = 0;
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

        self = Mathf.lerp(last, current, multiplier * t);

        multiplier++;
        return self;
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