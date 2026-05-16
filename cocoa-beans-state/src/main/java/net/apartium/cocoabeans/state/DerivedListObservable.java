package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ interface DerivedListObservable<E> extends ListObservable<E>, DerivedCollectionObservable<E, List<E>> {

    @Override
    default void add(int index, E element) { throw ListChainHelpers.unsupportedAdd(); }

    @Override
    default E remove(int index) { throw ListChainHelpers.unsupportedRemove(); }

    @Override
    default void sort(Comparator<? super E> comparator) { throw ListChainHelpers.unsupportedSort(); }

    @Override
    default ListObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return ListChainHelpers.filter(this, filter, collectionMapper(), constructCollection());
    }

    @Override
    default <T> ListObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return ListChainHelpers.filter(this, mapper, filter, collectionMapper(), constructCollection());
    }

    @Override
    default <R> ListObservable<R> mapEach(Function<E, R> mapper) {
        return ListChainHelpers.mapEach(this, mapper, collectionMapper(), constructCollection());
    }

    @Override
    default <R> ListObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return ListChainHelpers.flatMapEach(this, mapper, collectionMapper(), constructCollection());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    default <T> ListObservable<E> sorted(Function<E, Observable<T>> mapper, Observable<Comparator<? super T>> comparator) {
        return ListChainHelpers.sorted(this, mapper, comparator, ObservableCollectionType.toList((Function) collectionMapper(), (IntFunction) constructCollection()));
    }

}
