package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * {@code CollectorObservable} is a utility interface for creating and manipulating collections
 * in an observable context. It helps in creating snapshots, initializing collections with
 * specific capacities, and converting standard collections into observable collections.
 *
 * @param <E> the type of elements in the collection
 * @param <C> the type of the collection used in this collector
 * @param <R> the type of the resulting observable collection
 */
@ApiStatus.AvailableSince("0.0.50")
public interface CollectorObservable<
        E,
        C extends Collection<E>,
        R extends CollectionObservable<E, ? super C>
> {

    /**
     * Create a snapshot of the given collection
     * @param collection the collection to snapshot
     * @return a new collection containing the same elements as the given collection
     */
    C snapshot(Collection<E> collection);

    /**
     * Create a new collection with the given initial capacity
     * @param initialCapacity the initial capacity of the collection
     * @return a new collection with the given initial capacity
     */
    C collection(int initialCapacity);

    /**
     * Convert the given collection into an observable collection
     * @param base the collection to convert
     * @return an observable collection containing the same elements as the given collection
     */
    R asCollectionObservable(CollectionObservable<E, ? extends Collection<E>> base);

}
