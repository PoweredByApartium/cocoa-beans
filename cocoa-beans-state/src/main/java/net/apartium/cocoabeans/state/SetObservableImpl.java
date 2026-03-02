package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

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

    @Override
    public SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return new SetFilterObservable<>(this, filter);
    }

    @Override
    public <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return this.filter(element -> mapper.apply(element).map(filter::test));
    }
}
