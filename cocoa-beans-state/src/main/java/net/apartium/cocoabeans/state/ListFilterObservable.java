package net.apartium.cocoabeans.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/* package-private */ class ListFilterObservable<E> extends FilterObservable<E, List<E>> implements ListObservable<E> {

    public ListFilterObservable(Observable<List<E>> base, Function<E, Observable<Boolean>> filter) {
        super(base, filter, List::copyOf, ArrayList::new);
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
