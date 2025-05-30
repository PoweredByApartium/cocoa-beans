package net.apartium.cocoabeans.spigot.state;

import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class DoubleLerpObservable implements Observable<Double>, Observer {

    private final Observable<Double> source;
    private final Observable<Integer> tick;
    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());
    private final double t;
    private int multiplier = 0;


    private double self;
    private double last;
    private double start;

    private boolean dirty = false;

    public DoubleLerpObservable(Observable<Double> source, Observable<Integer> tick, Duration duration, Duration tickRate) {
        this.source = source;
        this.tick = tick;
        last = this.source.get();
        start = last;
        self = last;

        int totalJumps = (int) (duration.toMillis() / tickRate.toMillis());

        this.t = 1.0 / totalJumps;
        this.source.observe(this);
        this.tick.observe(this);
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

        self = lerp(last, current, multiplier * t);

        multiplier++;
        return self;
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
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
        if (dirty && tick == observable) {
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
