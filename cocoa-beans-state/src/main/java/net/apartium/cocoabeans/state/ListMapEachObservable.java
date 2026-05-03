package net.apartium.cocoabeans.state;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/* package-private */ class ListMapEachObservable<F, E> extends MapElementObservable<F, E, List<E>> implements ListObservable<E> {

    public ListMapEachObservable(Observable<? extends Collection<F>> base, Function<F, E> mapper, Function<Collection<E>, List<E>> collectionMapper, Function<Integer, ? extends Collection<E>> constructCollection) {
        super(base, mapper, collectionMapper, constructCollection);
    }

    @Override public void add(int index, E element) { throw new UnsupportedOperationException("ListFilterObservable does not support adding elements"); }
    @Override public E remove(int index) { throw new UnsupportedOperationException("ListFilterObservable does not support removing elements"); }
    @Override public void sort(Comparator<? super E> comparator) { throw new UnsupportedOperationException("ListFilterObservable does not support sorting"); }

    @Override
    public ListObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return new ListFilterObservable<>(this, filter);
    }

    @Override
    public <T> ListObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return this.filter(element -> mapper.apply(element).map(filter::test));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> ListObservable<R> mapEach(Function<E, R> mapper) {
        return new ListMapEachObservable<>(
                this,
                mapper,
                (Function) collectionMapper,
                (Function) constructCollection
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> ListObservable<R> flatMapEach(Function<E, Observable<R>> mapper) {
        return new ListFlatMapEachObservable<>(
                this,
                mapper,
                (Function) collectionMapper,
                (Function) constructCollection
        );
    }
}
