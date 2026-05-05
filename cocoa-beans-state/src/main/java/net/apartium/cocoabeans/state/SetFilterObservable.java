package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ class SetFilterObservable<E> extends FilterObservable<E, Set<E>> implements SetObservable<E> {

    public SetFilterObservable(Observable<Set<E>> base, Function<E, Observable<Boolean>> filter, Function<Collection<E>, Set<E>> copyOf, Function<Integer, ? extends Collection<E>> createInitSet) {
        super(base, filter, copyOf, createInitSet);
    }

    @Override
    public SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return SetChainHelpers.filter(this, filter, collectionMapper, constructCollection);
    }

    @Override
    public <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return SetChainHelpers.filter(this, mapper, filter, collectionMapper, constructCollection);
    }

    @Override
    public <R> SetObservable<R> mapEach(Function<E, R> mapper) {
        return SetChainHelpers.mapEach(this, mapper, collectionMapper, constructCollection);
    }

    @Override
    public <R> SetObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return SetChainHelpers.flatMapEach(this, mapper, collectionMapper, constructCollection);
    }
}
