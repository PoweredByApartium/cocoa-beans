package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * {@code ObservableCollectionType} is a utility interface for creating and manipulating collections
 * in an observable context. It helps in creating snapshots, initializing collections with
 * specific capacities, and converting standard collections into observable collections.
 *
 * @param <E> the type of elements in the collection
 * @param <C> the type of the collection used in this collector
 * @param <R> the type of the resulting observable collection
 */
@ApiStatus.AvailableSince("0.0.50")
public interface ObservableCollectionType<
        E,
        C extends Collection<E>,
        R extends CollectionObservable<E, ? super C>
> {

    /**
     * Creates a new {@link ObservableCollectionType} that uses {@link Set#copyOf(Collection)} as the snapshot function and {@link HashSet} as the collection function.
     * @return A new {@link ObservableCollectionType} instance.
     * @param <E> The type of elements in the collection.
     */
    @SuppressWarnings("unchecked")
    static <E> ObservableCollectionType<E, ? extends Set<E>, SetObservable<E>> toSet() {
        return (ObservableCollectionType<E, ? extends Set<E>, SetObservable<E>>) ObservableCollectionTypeImpl.SET;
    }

    /**
     * Creates a {@link ObservableCollectionType} that generates observable lists.
     * The resulting {@link ListObservable} can observe changes to a list collection.
     * This method uses {@link List#copyOf(Collection)} as the snapshot function and
     * {@link ArrayList} as the collection creation function.
     *
     * @param <E> The type of elements in the observable list.
     * @return A {@link ObservableCollectionType} configured to produce {@link ListObservable} instances.
     */
    @SuppressWarnings("unchecked")
    static <E> ObservableCollectionType<E, ? extends List<E>, ListObservable<E>> toList() {
        return (ObservableCollectionType<E, ? extends List<E>, ListObservable<E>>) ObservableCollectionTypeImpl.LIST;
    }

    /**
     * Creates a {@link ObservableCollectionType} that generates observable lists.
     * The resulting {@link ListObservable} can observe changes to a list collection.
     * This method allows specifying custom snapshot and collection creation functions.
     *
     * @param <E> The type of elements in the observable list.
     * @param snapshot A function that takes a {@link Collection} of elements and creates a snapshot {@link List}.
     * @param collection A function that takes an integer initial capacity and creates a new mutable {@link List}.
     * @return A {@link ObservableCollectionType} configured to produce {@link ListObservable} instances.
     */
    static <E> ObservableCollectionType<E, ? extends List<E>, ListObservable<E>> toList(
            Function<Collection<E>, ? extends List<E>> snapshot,
            IntFunction<? extends List<E>> collection
    ) {
        return new ObservableCollectionTypeImpl<>(snapshot, collection, ListObservableCopyOf::new);
    }

    /**
     * Creates a {@link ObservableCollectionType} that generates observable sets.
     * The resulting {@link SetObservable} can observe changes to a set collection.
     * This method allows specifying custom snapshot and collection creation functions.
     *
     * @param <E> The type of elements in the observable set.
     * @param snapshot A function that takes a {@link Collection} of elements and creates a snapshot {@link Set}.
     * @param collection A function that takes an integer initial capacity and creates a new mutable {@link Set}.
     * @return A {@link ObservableCollectionType} configured to produce {@link SetObservable} instances.
     */
    static <E> ObservableCollectionType<E, ? extends Set<E>, SetObservable<E>> toSet(
            Function<Collection<E>, ? extends Set<E>> snapshot,
            IntFunction<? extends Set<E>> collection
    ) {
        return new ObservableCollectionTypeImpl<>(snapshot, collection, SetObservableCopyOf::new);
    }

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
