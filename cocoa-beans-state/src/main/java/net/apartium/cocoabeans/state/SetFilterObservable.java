package net.apartium.cocoabeans.state;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/* package-private */ class SetFilterObservable<E> extends FilterObservable<E, Set<E>> implements SetObservable<E> {

    public SetFilterObservable(Observable<Set<E>> base, Function<E, Observable<Boolean>> filter, Function<Collection<E>, Set<E>> copyOf, Function<Integer, ? extends Collection<E>> createInitSet) {
        super(base, filter, copyOf, createInitSet);
    }

    @Override
    public SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return new SetFilterObservable<>(this, filter, this.collectionMapper, this.constructCollection);
    }

    @Override
    public <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return this.filter(element -> mapper.apply(element).map(filter::test));
    }

}
