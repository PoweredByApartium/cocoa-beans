package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ final class SetChainHelpers {

    private SetChainHelpers() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <F, R> SetObservable<R> mapEach(
            Observable<? extends Collection<F>> base,
            Function<F, R> mapper,
            Function<?, ?> collectionMapper,
            Function<?, ?> constructCollection
    ) {
        return new SetMapEachObservable<>(base, mapper, (Function) collectionMapper, (Function) constructCollection);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <F, R> SetObservable<R> flatMapEach(
            Observable<? extends Collection<F>> base,
            Function<F, Observable<R>> mapper,
            Function<?, ?> collectionMapper,
            Function<?, ?> constructCollection
    ) {
        return new SetFlatMapEachObservable<>(base, mapper, (Function) collectionMapper, (Function) constructCollection);
    }

    static <E> SetObservable<E> filter(
            Observable<Set<E>> base,
            Function<E, Observable<Boolean>> filter,
            Function<Collection<E>, Set<E>> copyOf,
            Function<Integer, ? extends Collection<E>> createInitSet
    ) {
        return new SetFilterObservable<>(base, filter, copyOf, createInitSet);
    }

    static <E, T> SetObservable<E> filter(
            Observable<Set<E>> base,
            Function<E, Observable<T>> mapper,
            Predicate<T> filter,
            Function<Collection<E>, Set<E>> copyOf,
            Function<Integer, ? extends Collection<E>> createInitSet
    ) {
        return filter(base, element -> mapper.apply(element).map(filter::test), copyOf, createInitSet);
    }

}
