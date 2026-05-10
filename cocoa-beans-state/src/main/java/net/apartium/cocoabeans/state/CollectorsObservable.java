package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * A factory class for creating {@link CollectorObservable} instances.
 */
@ApiStatus.AvailableSince("0.0.50")
public class CollectorsObservable {

    private CollectorsObservable() {}

    /**
     * Creates a new {@link CollectorObservable} that uses {@link Set#copyOf(Collection)} as the snapshot function and {@link HashSet} as the collection function.
     * @return A new {@link CollectorObservable} instance.
     * @param <E> The type of elements in the collection.
     */
    public static <E> CollectorObservable<E, ? extends Set<E>, SetObservable<E>> toSet() {
        return toSet(Set::copyOf, HashSet::new);
    }

    /**
     * Creates a {@link CollectorObservable} that generates observable sets.
     * The resulting {@link SetObservable} can observe changes to a set collection.
     * This method allows specifying custom snapshot and collection creation functions.
     *
     * @param <E> The type of elements in the observable set.
     * @param snapshot A function that takes a {@link Collection} of elements and creates a snapshot {@link Set}.
     * @param collection A function that takes an integer initial capacity and creates a new mutable {@link Set}.
     * @return A {@link CollectorObservable} configured to produce {@link SetObservable} instances.
     */
    public static <E> CollectorObservable<E, ? extends Set<E>, SetObservable<E>> toSet(
            Function<Collection<E>, ? extends Set<E>> snapshot,
            IntFunction<? extends Set<E>> collection
    ) {
        return new CollectorObservable<>() {
            @Override
            public Set<E> snapshot(Collection<E> collection) {
                return snapshot.apply(collection);
            }

            @Override
            public Set<E> collection(int initialCapacity) {
                return collection.apply(initialCapacity);
            }

            @Override
            public SetObservable<E> asCollectionObservable(CollectionObservable<E, ? extends Collection<E>> base) {
                return new SetObservableCopyOf<>(
                        base,
                        this
                );
            }
        };
    }

    /**
     * Creates a {@link CollectorObservable} that generates observable lists.
     * The resulting {@link ListObservable} can observe changes to a list collection.
     * This method uses {@link List#copyOf(Collection)} as the snapshot function and
     * {@link ArrayList} as the collection creation function.
     *
     * @param <E> The type of elements in the observable list.
     * @return A {@link CollectorObservable} configured to produce {@link ListObservable} instances.
     */
    public static <E> CollectorObservable<E, ? extends List<E>, ListObservable<E>> toList() {
        return toList(List::copyOf, ArrayList::new);
    }

    /**
     * Creates a {@link CollectorObservable} that generates observable lists.
     * The resulting {@link ListObservable} can observe changes to a list collection.
     * This method allows specifying custom snapshot and collection creation functions.
     *
     * @param <E> The type of elements in the observable list.
     * @param snapshot A function that takes a {@link Collection} of elements and creates a snapshot {@link List}.
     * @param collection A function that takes an integer initial capacity and creates a new mutable {@link List}.
     * @return A {@link CollectorObservable} configured to produce {@link ListObservable} instances.
     */
    public static <E> CollectorObservable<E, ? extends List<E>, ListObservable<E>> toList(
            Function<Collection<E>, ? extends List<E>> snapshot,
            IntFunction<? extends List<E>> collection
    ) {
        return new CollectorObservable<>() {
            @Override
            public List<E> snapshot(Collection<E> collection) {
                return snapshot.apply(collection);
            }

            @Override
            public List<E> collection(int initialCapacity) {
                return collection.apply(initialCapacity);
            }

            @Override
            public ListObservable<E> asCollectionObservable(CollectionObservable<E, ? extends Collection<E>> base) {
                return new ListObservableCopyOf<>(
                        base,
                        this
                );
            }
        };
    }


}
