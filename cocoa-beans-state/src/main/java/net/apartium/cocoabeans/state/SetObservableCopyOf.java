package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ class SetObservableCopyOf<E> implements SetObservable<E>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private final CollectionObservable<E, ? extends Collection<E>> base;
    private final Observable<Integer> size;
    private final CollectorObservable<E, Set<E>, SetObservable<E>> collector;

    private Set<E> cache;
    private boolean dirty = true;

    SetObservableCopyOf(
            CollectionObservable<E, ? extends Collection<E>> base,
            CollectorObservable<E, Set<E>, SetObservable<E>> collector
    ) {
        this.base = base;
        this.collector = collector;

        this.base.observe(this);

        this.size = this.map(Set::size);
    }

    public Set<E> get() {
        if (!dirty)
            return cache;

        cache = collector.snapshot(base.get());
        dirty = false;

        return cache;
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (observable != base)
            return;

        dirty = true;
        for (Observer observer : Set.copyOf(observers))
            observer.flagAsDirty(this);
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }

    @Override public boolean add(E element) {throw new UnsupportedOperationException("SetObservableCopyOf does not support adding elements");}
    @Override public boolean remove(E element) {throw new UnsupportedOperationException("SetObservableCopyOf does not support removing elements");}
    @Override public boolean addAll(Collection<? extends E> collection) {throw new UnsupportedOperationException("SetObservableCopyOf does not support adding elements");}
    @Override public boolean removeAll(Collection<? extends E> collection) {throw new UnsupportedOperationException("SetObservableCopyOf does not support removing elements");}
    @Override public boolean removeIf(Predicate<? super E> filter) {throw new UnsupportedOperationException("SetObservableCopyOf does not support removing elements");}
    @Override public boolean retainAll(Collection<? extends E> collection) {throw new UnsupportedOperationException("SetObservableCopyOf does not support removing elements");}
    @Override public void clear() {throw new UnsupportedOperationException("SetObservableCopyOf does not support removing elements");}

    @Override
    public Observable<Integer> size() {
        return size;
    }

    @Override
    public SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return SetChainHelpers.filter(this, filter, collector::snapshot, collector::collection);
    }

    @Override
    public <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return SetChainHelpers.filter(this, mapper, filter, collector::snapshot, collector::collection);
    }

    @Override
    public <R> SetObservable<R> mapEach(Function<E, R> mapper) {
        return SetChainHelpers.mapEach(this, mapper, collector::snapshot, collector::collection);
    }

    @Override
    public <R> SetObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return SetChainHelpers.flatMapEach(this, mapper, collector::snapshot, collector::collection);
    }

}
