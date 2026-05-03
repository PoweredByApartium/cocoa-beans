package net.apartium.cocoabeans.state;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/* package-private */ class SetFlatMapEachObservable<F, E> extends FlatMapElementObservable<F, E, Set<E>> implements SetObservable<E> {

    public SetFlatMapEachObservable(Observable<? extends Collection<F>> base, Function<F, Observable<E>> mapper, Function<Collection<E>, Set<E>> collectionMapper, Function<Integer, ? extends Collection<E>> constructCollection) {
        super(base, mapper, collectionMapper, constructCollection);
    }

    @Override
    public SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return new SetFilterObservable<>(this, filter, this.collectionMapper, this.constructCollection);
    }

    @Override
    public <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return this.filter(element -> mapper.apply(element).map(filter::test));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> SetObservable<R> mapEach(Function<E, R> mapper) {
        return new SetMapEachObservable<>(
                this,
                mapper,
                (Function) collectionMapper,
                (Function) constructCollection
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> SetObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return new SetFlatMapEachObservable<>(
                this,
                mapper,
                (Function) collectionMapper,
                (Function) constructCollection
        );
    }
}
