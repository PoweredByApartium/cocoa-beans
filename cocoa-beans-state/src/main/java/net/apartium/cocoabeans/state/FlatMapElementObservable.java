package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A specialized observable implementation that flattens elements of a source observable
 * into a single collection, applying a provided mapping function to transform each element
 * from the source into an observable of a target type.
 * </p>
 * The class also manages the dynamic updating of its internal collection whenever changes
 * occur in the source observable or the nested observables it depends upon.
 *
 * @param <F> The type of elements in the source observable.
 * @param <E> The type of elements in the resulting flattened collection.
 * @param <C> The specific type of collection used to hold the flattened elements.
 */
@ApiStatus.AvailableSince("0.0.50")
public class FlatMapElementObservable<F, E, C extends Collection<E>> implements CollectionObservable<E, C>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private final Observable<? extends Collection<F>> base;

    private final Function<F, Observable<E>> mapper;
    protected final Function<Collection<E>, C> collectionMapper;
    protected final Function<Integer, ? extends Collection<E>> constructCollection;

    private final Map<F, Observable<E>> innerByElement = new LinkedHashMap<>();
    private final Map<Observable<E>, Set<F>> dependsOn = new IdentityHashMap<>();

    private boolean baseDirty = true;
    private boolean innerDirty = true;

    private C cachedCollection;

    private final Observable<Integer> size;

    public FlatMapElementObservable(
            Observable<? extends Collection<F>> base,
            Function<F, Observable<E>> mapper,
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

    private void updateBase() {
        if (!baseDirty)
            return;

        Collection<F> source = base.get();

        Set<F> keepElements = Collections.newSetFromMap(new IdentityHashMap<>());
        keepElements.addAll(source);

        Iterator<Map.Entry<F, Observable<E>>> elementIterator = innerByElement.entrySet().iterator();
        while (elementIterator.hasNext()) {
            Map.Entry<F, Observable<E>> entry = elementIterator.next();

            if (keepElements.contains(entry.getKey()))
                continue;

            removeInnerDependency(entry.getKey(), entry.getValue());
            elementIterator.remove();
        }

        for (F element : source) {
            if (innerByElement.containsKey(element))
                continue;

            Observable<E> inner = mapper.apply(element);
            innerByElement.put(element, inner);

            if (inner != null) {
                Set<F> elements = dependsOn.computeIfAbsent(inner, key -> {
                    key.observe(this);
                    return Collections.newSetFromMap(new IdentityHashMap<>());
                });

                elements.add(element);
            }
        }

        baseDirty = false;
        innerDirty = true;
    }

    private void removeInnerDependency(F element, Observable<E> inner) {
        if (inner == null)
            return;

        Set<F> elements = dependsOn.get(inner);
        if (elements == null)
            return;

        elements.remove(element);

        if (!elements.isEmpty())
            return;

        inner.removeObserver(this);
        dependsOn.remove(inner);
    }

    @Override
    public C get() {
        if (!baseDirty && !innerDirty)
            return cachedCollection;

        updateBase();

        Collection<E> mapped = constructCollection.apply(innerByElement.size());

        for (Observable<E> observable : innerByElement.values()) {
            if (observable == null)
                mapped.add(null);
            else
                mapped.add(observable.get());
        }

        cachedCollection = collectionMapper.apply(mapped);
        innerDirty = false;

        return cachedCollection;
    }

    protected void notifyObservers() {
        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (observable == base) {
            baseDirty = true;
            notifyObservers();
            return;
        }

        if (dependsOn.containsKey(observable)) {
            innerDirty = true;
            notifyObservers();
        }
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }

    @Override public boolean add(E element) { throw new UnsupportedOperationException("FlatMapElementObservable does not support adding elements"); }

    @Override public boolean remove(E element) { throw new UnsupportedOperationException("FlatMapElementObservable does not support removing elements"); }

    @Override public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("FlatMapElementObservable does not support adding elements"); }

    @Override public boolean removeAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("FlatMapElementObservable does not support removing elements"); }

    @Override public boolean removeIf(Predicate<? super E> filter) { throw new UnsupportedOperationException("FlatMapElementObservable does not support removing elements"); }

    @Override public boolean retainAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("FlatMapElementObservable does not support removing elements"); }

    @Override public void clear() { throw new UnsupportedOperationException("FlatMapElementObservable does not support removing elements"); }

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