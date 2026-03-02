package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ApiStatus.Internal
/* package-private */ class SetObservableImpl<E> extends AbstractCollectionObservable<E, Set<E>> implements SetObservable<E> {


    public SetObservableImpl() {
        this(new HashSet<>());
    }

    public SetObservableImpl(Set<E> set) {
        super(set);
    }

    @Override
    public Set<E> get() {
        return Set.copyOf(this.collection);
    }

    @Override
    protected Set<E> createFilteredCollection(Collection<E> elements) {
        return Set.copyOf(elements);
    }

    @Override
    protected Set<E> createCollection(int initialCapacity) {
        return new HashSet<>(initialCapacity);
    }
}
