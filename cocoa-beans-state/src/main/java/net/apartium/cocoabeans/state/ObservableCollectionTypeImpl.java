package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * @hidden
 * @param <E>
 * @param <C>
 * @param <R>
 */
@ApiStatus.Internal
/* package-private */ class ObservableCollectionTypeImpl<E, C extends Collection<E>, R extends CollectionObservable<E, ? super C>>
        implements ObservableCollectionType<E, C, R> {

    /* package-private */ static final ObservableCollectionType<?, ? extends Set<?>, ? extends SetObservable<?>> SET = ObservableCollectionType.toSet(Set::copyOf, HashSet::new);

    /* package-private */ static final ObservableCollectionType<?, ? extends List<?>, ? extends ListObservable<?>> LIST = ObservableCollectionType.toList(List::copyOf, ArrayList::new);

    private final Function<Collection<E>, ? extends C> snapshot;
    private final IntFunction<? extends C> collection;
    private final CopyOfFactory<E, C, R> factory;

    public ObservableCollectionTypeImpl(Function<Collection<E>, ? extends C> snapshot, IntFunction<? extends C> collection,
                                        CopyOfFactory<E, C, R> factory) {
        this.snapshot = snapshot;
        this.collection = collection;
        this.factory = factory;
    }

    @Override
    public C snapshot(Collection<E> col) {
        return snapshot.apply(col);
    }

    @Override
    public C collection(int initialCapacity) {
        return collection.apply(initialCapacity);
    }

    @Override
    public R asCollectionObservable(CollectionObservable<E, ? extends Collection<E>> base) {
        return factory.create(base, this);
    }

    @FunctionalInterface
    interface CopyOfFactory<E, C extends Collection<E>, R extends CollectionObservable<E, ? super C>> {
        R create(CollectionObservable<E, ? extends Collection<E>> base, ObservableCollectionType<E, C, R> collector);
    }


}
