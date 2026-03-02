package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.CollectionHelpers;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ class ListObservableImpl<E> extends AbstractCollectionObservable<E, List<E>> implements ListObservable<E> {


    public ListObservableImpl() {
        this(new ArrayList<>());
    }

    public ListObservableImpl(List<E> list) {
        super(list);
    }


    @Override
    public void add(int index, E element) {
        int prevSize = collection.size();
        collection.add(index, element);

        if (prevSize == collection.size())
            return;

        notifyObservers();
    }

    @Override
    public E remove(int index) {
        E element = collection.remove(index);

        if (element == null)
            return null;

        notifyObservers();

        return element;
    }

    @Override
    public void sort(Comparator<? super E> comparator) {
        if (comparator == null)
            throw new NullPointerException();

        if (CollectionHelpers.isSorted(collection, comparator))
            return;

        collection.sort(comparator);
        notifyObservers();
    }

    @Override
    public List<E> get() {
        return List.copyOf(collection);
    }

    @Override
    protected List<E> createFilteredCollection(Collection<E> elements) {
        return List.copyOf(elements);
    }

    @Override
    protected List<E> createCollection(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }

    @Override
    public ListObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        return new ListFilterObservable<>(this, filter);
    }

    @Override
    public <T> ListObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return this.filter(element -> mapper.apply(element).map(filter::test));
    }

}
