package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ class SetObservableImpl<E> extends AbstractCollectionObservable<E, Set<E>> implements SetObservable<E> {


    private final Function<Collection<E>, Set<E>> copyOf;
    private final Function<Integer, Set<E>> createInitSet;

    public SetObservableImpl() {
        this(new HashSet<>());
    }

    public SetObservableImpl(Set<E> set) {
        this(set, Set::copyOf);
    }

    public SetObservableImpl(Set<E> set, Function<Collection<E>, Set<E>> copyOf) {
        this(set, copyOf, HashSet::new);
    }

    public SetObservableImpl(Set<E> set, Function<Collection<E>, Set<E>> copyOf, Function<Integer, Set<E>> createInitSet) {
        super(set);

        this.copyOf = copyOf;
        this.createInitSet = createInitSet;
    }


    @Override
    public Set<E> get() {
        return copyOf.apply(this.collection);
    }

    @Override
    protected Set<E> createFilteredCollection(Collection<E> elements) {
        return copyOf.apply(elements);
    }

    @Override
    protected Set<E> createCollection(int initialCapacity) {
        return createInitSet.apply(initialCapacity);
    }

    @Override
    public SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return SetChainHelpers.filter(this, filter, copyOf, createInitSet);
    }

    @Override
    public <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return SetChainHelpers.filter(this, mapper, filter, copyOf, createInitSet);
    }

    @Override
    public <R> SetObservable<R> mapEach(Function<E, R> mapper) {
        return SetChainHelpers.mapEach(this, mapper, copyOf, createInitSet);
    }

    @Override
    public <R> SetObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return SetChainHelpers.flatMapEach(this, mapper, copyOf, createInitSet);
    }

}
