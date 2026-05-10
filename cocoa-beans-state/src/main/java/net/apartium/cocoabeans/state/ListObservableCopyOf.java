package net.apartium.cocoabeans.state;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/* package-private */ class ListObservableCopyOf<E> implements ListObservable<E>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private final CollectionObservable<E, ? extends Collection<E>> base;
    private final Observable<Integer> size;
    private final CollectorObservable<E, List<E>, ListObservable<E>> collector;

    private List<E> cache;
    private boolean dirty = true;

    ListObservableCopyOf(CollectionObservable<E, ? extends Collection<E>> base, CollectorObservable<E, List<E>, ListObservable<E>> collector) {
        this.base = base;
        this.collector = collector;

        this.base.observe(this);

        this.size = this.map(List::size);
    }

    @Override
    public List<E> get() {
        if (!dirty)
            return cache;

        cache = collector.snapshot(base.get());
        dirty = false;

        return cache;
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
        if (observable != base)
            return;

        dirty = true;
        for (Observer observer : Set.copyOf(observers))
            observer.flagAsDirty(this);
    }

    @Override public boolean add(E element) {throw new UnsupportedOperationException("ListObservableCopyOf does not support adding elements");}
    @Override public boolean remove(E element) {throw new UnsupportedOperationException("ListObservableCopyOf does not support removing elements");}
    @Override public boolean addAll(Collection<? extends E> collection) {throw new UnsupportedOperationException("ListObservableCopyOf does not support adding elements");}
    @Override public boolean removeAll(Collection<? extends E> collection) {throw new UnsupportedOperationException("ListObservableCopyOf does not support removing elements");}
    @Override public boolean removeIf(Predicate<? super E> filter) {throw new UnsupportedOperationException("ListObservableCopyOf does not support removing elements");}
    @Override public boolean retainAll(Collection<? extends E> collection) {throw new UnsupportedOperationException("ListObservableCopyOf does not support removing elements");}
    @Override public void clear() {throw new UnsupportedOperationException("ListObservableCopyOf does not support removing elements");}
    @Override public void add(int index, E element) {throw new UnsupportedOperationException("ListObservableCopyOf does not support adding elements");}
    @Override public E remove(int index) {throw new UnsupportedOperationException("ListObservableCopyOf does not support removing elements");}
    @Override public void sort(Comparator<? super E> comparator) {throw new UnsupportedOperationException("ListObservableCopyOf does not support sorting");}


    @Override
    public Observable<Integer> size() {
        return size;
    }
    @Override
    public ListObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return ListChainHelpers.filter(this, filter, collector::snapshot, collector::collection);
    }

    @Override
    public <T> ListObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return ListChainHelpers.filter(this, mapper, filter, collector::snapshot, collector::collection);
    }

    @Override
    public <R> ListObservable<R> mapEach(Function<E, R> mapper) {
        return ListChainHelpers.mapEach(this, mapper, collector::snapshot, collector::collection);
    }

    @Override
    public <R> ListObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return ListChainHelpers.flatMapEach(this, mapper, collector::snapshot, collector::collection);
    }

}
