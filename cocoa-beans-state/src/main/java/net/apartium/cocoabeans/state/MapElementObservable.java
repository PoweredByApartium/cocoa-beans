package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An observable implementation that maps the elements of one collection to another based on a provided mapping function.
 * This class observes changes in the base observable and ensures the mapped collection is updated accordingly.
 *
 * @param <F> The type of elements in the source observable collection.
 * @param <E> The type of elements in the mapped observable collection.
 * @param <C> The type of the mapped collection.
 * @see CollectionObservable
 * @see Observable
 * @see Observer
 */
@ApiStatus.AvailableSince("0.0.50")
public class MapElementObservable<F, E, C extends Collection<E>> implements CollectionObservable<E, C>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private final Observable<? extends Collection<F>> base;

    private final Function<F, E> mapper;
    protected final Function<Collection<E>, C> collectionMapper;
    protected final Function<Integer, ? extends Collection<E>> constructCollection;

    private final Observable<Integer> size;

    private boolean baseDirty = true;
    private C cachedCollection;

    /**
     * Constructs a new instance of the MapElementObservable class.
     *
     * @param base The base observable to be transformed. Changes in the base observable will be reflected in this observable.
     * @param mapper A function used to transform elements of type F from the base observable into elements of type E.
     * @param collectionMapper A function that takes a collection of mapped elements and transforms it into a collection of type C.
     * @param constructCollection A function used to create a new collection of type E of a specified size.
     */
    public MapElementObservable(
            Observable<? extends Collection<F>> base,
            Function<F, E> mapper,
            Function<Collection<E>, C> collectionMapper,
            Function<Integer, ? extends Collection<E>> constructCollection
    ) {
        this.base = base;
        this.base.observe(this);

        this.mapper = mapper;
        this.collectionMapper = collectionMapper;
        this.constructCollection = constructCollection;

        this.size = this.map(Collection::size);
    }

    @Override
    public C get() {
        if (!baseDirty)
            return cachedCollection;

        Collection<F> source = base.get();
        Collection<E> mapped = constructCollection.apply(source.size());

        for (F element : source)
            mapped.add(mapper.apply(element));

        cachedCollection = collectionMapper.apply(mapped);
        baseDirty = false;

        return cachedCollection;
    }

    protected void notifyObservers() {
        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (observable != base)
            return;

        baseDirty = true;
        notifyObservers();
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }

    @Override public boolean add(E element) { throw new UnsupportedOperationException("MapElementObservable does not support adding elements"); }

    @Override public boolean remove(E element) { throw new UnsupportedOperationException("MapElementObservable does not support removing elements"); }

    @Override public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("MapElementObservable does not support adding elements"); }

    @Override public boolean removeAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("MapElementObservable does not support removing elements"); }

    @Override public boolean removeIf(Predicate<? super E> filter) { throw new UnsupportedOperationException("MapElementObservable does not support removing elements"); }

    @Override public boolean retainAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("MapElementObservable does not support removing elements"); }

    @Override public void clear() { throw new UnsupportedOperationException("MapElementObservable does not support removing elements"); }

    @Override
    public Observable<Integer> size() {
        return size;
    }

    @Override
    public CollectionObservable<E, C> filter(Function<E, Observable<Boolean>> filter) {
        return new FilterObservable<>(this, filter, collectionMapper, constructCollection);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> CollectionObservable<R, ? extends Collection<R>> mapEach(Function<E, R> mapper) {
        return new MapElementObservable<>(
                this,
                mapper,
                (Function) collectionMapper,
                (Function) constructCollection
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <R> CollectionObservable<R, ? extends Collection<R>> flatMapEach(Function<E, Observable<R>> mapper) {
        return new FlatMapElementObservable<>(
                this,
                mapper,
                (Function) collectionMapper,
                (Function) constructCollection
        );
    }

}
