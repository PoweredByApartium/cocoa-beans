package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ class ListMapEachObservable<F, E> extends MapElementObservable<F, E, List<E>> implements ListObservable<E> {

    public ListMapEachObservable(Observable<? extends Collection<F>> base, Function<F, E> mapper, Function<Collection<E>, List<E>> collectionMapper, Function<Integer, ? extends Collection<E>> constructCollection) {
        super(base, mapper, collectionMapper, constructCollection);
    }

    @Override public void add(int index, E element) { throw ListChainHelpers.unsupportedAdd(); }
    @Override public E remove(int index) { throw ListChainHelpers.unsupportedRemove(); }
    @Override public void sort(Comparator<? super E> comparator) { throw ListChainHelpers.unsupportedSort(); }

    @Override
    public ListObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return ListChainHelpers.filter(this, filter);
    }

    @Override
    public <T> ListObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return ListChainHelpers.filter(this, mapper, filter);
    }

    @Override
    public <R> ListObservable<R> mapEach(Function<E, R> mapper) {
        return ListChainHelpers.mapEach(this, mapper, collectionMapper, constructCollection);
    }

    @Override
    public <R> ListObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return ListChainHelpers.flatMapEach(this, mapper, collectionMapper, constructCollection);
    }
}
