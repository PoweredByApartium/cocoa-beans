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
        return new SetFilterObservable<>(this, filter, copyOf, createInitSet);
    }

    @Override
    public <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return this.filter(element -> mapper.apply(element).map(filter::test));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> SetMapEachObservable<E, R> mapEach(Function<E, R> mapper) {
        return new SetMapEachObservable<>(
                this,
                mapper,
                (Function) copyOf,
                (Function) createInitSet
        );
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> SetObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return new SetFlatMapEachObservable<>(
                this,
                mapper,
                (Function) copyOf,
                (Function) createInitSet
        );
    }

}
