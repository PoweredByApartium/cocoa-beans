package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ interface DerivedSetObservable<E> extends SetObservable<E>, DerivedCollectionObservable<E, Set<E>> {

    @Override
    default SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return SetChainHelpers.filter(this, filter, collectionMapper(), constructCollection());
    }

    @Override
    default <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return SetChainHelpers.filter(this, mapper, filter, collectionMapper(), constructCollection());
    }

    @Override
    default <R> SetObservable<R> mapEach(Function<E, R> mapper) {
        return SetChainHelpers.mapEach(this, mapper, collectionMapper(), constructCollection());
    }

    @Override
    default <R> SetObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return SetChainHelpers.flatMapEach(this, mapper, collectionMapper(), constructCollection());
    }
}
