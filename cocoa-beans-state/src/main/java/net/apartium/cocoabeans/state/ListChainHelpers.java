package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ final class ListChainHelpers {

    private static final String NO_ADD = "ListObservable derived view does not support adding elements";
    private static final String NO_REMOVE = "ListObservable derived view does not support removing elements";
    private static final String NO_SORT = "ListObservable derived view does not support sorting";

    private ListChainHelpers() {}

    static UnsupportedOperationException unsupportedAdd() {
        return new UnsupportedOperationException(NO_ADD);
    }

    static UnsupportedOperationException unsupportedRemove() {
        return new UnsupportedOperationException(NO_REMOVE);
    }

    static UnsupportedOperationException unsupportedSort() {
        return new UnsupportedOperationException(NO_SORT);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <F, R, E> ListObservable<R> mapEach(
            Observable<? extends Collection<F>> base,
            Function<F, R> mapper,
            Function<Collection<E>, List<E>> collectionMapper,
            IntFunction<? extends Collection<E>> constructCollection
    ) {
        return new MappedListObservable<>(base, mapper, (Function) collectionMapper, (IntFunction) constructCollection);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <F, R, E> ListObservable<R> flatMapEach(
            Observable<? extends Collection<F>> base,
            Function<F, Observable<R>> mapper,
            Function<Collection<E>, List<E>> collectionMapper,
            IntFunction<? extends Collection<E>> constructCollection
    ) {
        return new FlatMappedListObservable<>(base, mapper, (Function) collectionMapper, (IntFunction) constructCollection);
    }

    static <E> ListObservable<E> filter(
            Observable<List<E>> base,
            Function<E, Observable<Boolean>> filter,
            Function<Collection<E>, List<E>> collectionMapper,
            IntFunction<? extends Collection<E>> constructCollection
    ) {
        return new FilteredListObservable<>(base, filter, collectionMapper, constructCollection);
    }

    static <E, T> ListObservable<E> filter(
            Observable<List<E>> base,
            Function<E, Observable<T>> mapper,
            Predicate<T> filter,
            Function<Collection<E>, List<E>> collectionMapper,
            IntFunction<? extends Collection<E>> constructCollection
    ) {
        return filter(base, element -> mapper.apply(element).map(filter::test), collectionMapper, constructCollection);
    }

    static <E, T> ListObservable<E> sorted(
            Observable<? extends Collection<E>> base,
            Function<E, Observable<T>> mapper,
            Observable<Comparator<? super T>> comparator,
            ObservableCollectionType<E, ? extends List<E>, ListObservable<E>> type
    ) {
        return new SortedListObservable<>(base, mapper, comparator, type);
    }

}
