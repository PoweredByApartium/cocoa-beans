package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ class SetFlatMapEachObservable<F, E> extends FlatMapElementObservable<F, E, Set<E>> implements SetObservable<E> {

    public SetFlatMapEachObservable(Observable<? extends Collection<F>> base, Function<F, Observable<E>> mapper, Function<Collection<E>, Set<E>> collectionMapper, Function<Integer, ? extends Collection<E>> constructCollection) {
        super(base, mapper, collectionMapper, constructCollection);
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
