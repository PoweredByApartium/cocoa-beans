package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A class that observes a base collection and provides a filtered view of it based on
 * a specified condition. The filtering is dynamic, updating the observed collection when
 * the base collection or the filter's observables change.
 *
 * @param <E> The type of elements contained in the collection.
 * @param <C> The type of collection being observed and filtered.
 */
@ApiStatus.AvailableSince("0.0.46")
public class FilterObservable<E, C extends Collection<E>> implements CollectionObservable<E, C>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private final Observable<C> base;

    private final Function<E, Observable<Boolean>> filter;
    protected final Function<Collection<E>, C> collectionMapper;
    protected final Function<Integer, ? extends Collection<E>> constructCollection;

    private boolean baseDirty = true;
    private final Set<Observable<Boolean>> flagged = Collections.newSetFromMap(new IdentityHashMap<>());

    private final Map<Observable<Boolean>, Boolean> cacheValueMap = new IdentityHashMap<>();
    private final Map<Observable<Boolean>, Set<E>> dependsOn = new IdentityHashMap<>();
    private final Map<E, Observable<Boolean>> observableForElement = new IdentityHashMap<>();

    private Collection<E> baseSnapshot;

    private final Observable<Integer> size;
    private C cachedCollection;

    /**
     * Constructs a new FilterObservable, which applies a dynamic filtering mechanism on a base observable
     * collection. The filtering, collection construction, and mapping of collections are handled by the provided
     * functional interfaces.
     *
     * @param base               The base observable collection to be filtered and observed.
     * @param filter             A function that takes an element and returns an observable boolean, indicating
     *                           whether the element should be included in the filtered collection.
     * @param collectionMapper   A function that maps a collection of filtered elements to the desired target
     *                           collection type.
     * @param constructCollection A function that constructs an empty collection of the desired type based on the
     *                           provided size.
     */
    public FilterObservable(Observable<C> base, Function<E, Observable<Boolean>> filter, Function<Collection<E>, C> collectionMapper, Function<Integer, ? extends Collection<E>> constructCollection) {
        this.base = base;
        this.base.observe(this);

        this.filter = filter;
        this.collectionMapper = collectionMapper;
        this.constructCollection = constructCollection;

        this.size = this.map(Collection::size);
    }

    private void checkAndUpdateBase() {
        C newSource = base.get();
        baseSnapshot = newSource;

        Set<E> seenElements = Collections.newSetFromMap(new IdentityHashMap<>());
        seenElements.addAll(newSource);

        Iterator<Map.Entry<E, Observable<Boolean>>> elementIterator = observableForElement.entrySet().iterator();
        while (elementIterator.hasNext()) {
            Map.Entry<E, Observable<Boolean>> entry = elementIterator.next();
            if (seenElements.contains(entry.getKey()))
                continue;

            removeElementDependency(entry.getKey(), entry.getValue());
            elementIterator.remove();
        }

        for (E element : newSource) {
            if (observableForElement.containsKey(element))
                continue;

            Observable<Boolean> observable = filter.apply(element);
            observableForElement.put(element, observable);

            Set<E> elements = dependsOn.computeIfAbsent(observable, key -> {
                key.observe(this);
                return Collections.newSetFromMap(new IdentityHashMap<>());
            });
            elements.add(element);
        }

        baseDirty = false;
    }

    private void removeElementDependency(E element, Observable<Boolean> observable) {
        Set<E> elements = dependsOn.get(observable);
        if (elements == null)
            return;

        elements.remove(element);
        if (!elements.isEmpty())
            return;

        observable.removeObserver(this);
        dependsOn.remove(observable);
        cacheValueMap.remove(observable);
        flagged.remove(observable);
    }

    public C get() {
        if (!baseDirty && flagged.isEmpty() && cachedCollection != null)
            return cachedCollection;

        if (baseDirty)
            checkAndUpdateBase();

        for (Observable<Boolean> observable : flagged)
            cacheValueMap.put(observable, observable.get());
        flagged.clear();

        Collection<E> newCollection = constructCollection.apply(baseSnapshot.size());
        for (E element : baseSnapshot) {
            Observable<Boolean> observable = observableForElement.get(element);
            Boolean value = cacheValueMap.computeIfAbsent(observable, key -> {
                boolean initialValue = key.get();
                cacheValueMap.put(key, initialValue);
                return initialValue;
            });

            if (value)
                newCollection.add(element);
        }

        cachedCollection = collectionMapper.apply(newCollection);
        return cachedCollection;
    }

    /**
     * Notifies all observers of the change.
     */
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
            flagged.add((Observable<Boolean>) observable);
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

    @Override public boolean add(E element) { throw new UnsupportedOperationException("FilterObservable does not support adding elements"); }

    @Override public boolean remove(E element) { throw new UnsupportedOperationException("FilterObservable does not support removing elements"); }

    @Override public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("FilterObservable does not support adding elements"); }

    @Override public boolean removeAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("FilterObservable does not support removing elements"); }

    @Override public boolean removeIf(Predicate<? super E> filter) { throw new UnsupportedOperationException("FilterObservable does not support removing elements"); }

    @Override public boolean retainAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("FilterObservable does not support removing elements"); }

    @Override public void clear() { throw new UnsupportedOperationException("FilterObservable does not support removing elements"); }

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
