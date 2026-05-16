package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Internal interface that provides shared default implementations for the {@code filter},
 * {@code mapEach}, and {@code flatMapEach} chain methods on derived (read-only)
 * {@link CollectionObservable} views.
 *
 * @param <E> the element type
 * @param <C> the collection type
 */
@ApiStatus.Internal
/* package-private */ interface DerivedCollectionObservable<E, C extends Collection<E>> extends CollectionObservable<E, C> {

    Function<Collection<E>, C> collectionMapper();

    IntFunction<? extends Collection<E>> constructCollection();

    @Override
    default CollectionObservable<E, C> filter(Function<E, Observable<Boolean>> filter) {
        return new FilterObservable<>(this, filter, collectionMapper(), constructCollection());
    }

    @SuppressWarnings("rawtypes")
    @Override
    default <R> CollectionObservable<R, ? extends Collection<R>> mapEach(Function<E, R> mapper) {
        return new MapElementObservable<>(
                this,
                mapper,
                (Function) collectionMapper(),
                (IntFunction) constructCollection()
        );
    }

    @SuppressWarnings("rawtypes")
    @Override
    default <R> CollectionObservable<R, ? extends Collection<R>> flatMapEach(Function<E, Observable<R>> mapper) {
        return new FlatMapElementObservable<>(
                this,
                mapper,
                (Function) collectionMapper(),
                (IntFunction) constructCollection()
        );
    }
}
