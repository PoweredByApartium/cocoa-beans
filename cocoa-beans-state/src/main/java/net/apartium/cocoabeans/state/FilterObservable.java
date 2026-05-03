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
    private final Set<Observable<Boolean>> flagged = new HashSet<>();

    private final Map<Observable<Boolean>, Boolean> cacheValueMap = new HashMap<>();
    private final Map<Observable<Boolean>, Set<E>> dependsOn = new LinkedHashMap<>();


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
        C newDepends = base.get();
        Set<Observable<Boolean>> keep = Collections.newSetFromMap(new IdentityHashMap<>());

        for (E e : newDepends) {
            Observable<Boolean> observable = filter.apply(e);
            keep.add(observable);

            boolean prev = dependsOn.computeIfAbsent(observable, key -> new LinkedHashSet<>()).add(e);
            if (prev)
                observable.observe(this);
        }

        Iterator<Map.Entry<Observable<Boolean>, Set<E>>> it = dependsOn.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Observable<Boolean>, Set<E>> entry = it.next();
            if (!keep.contains(entry.getKey())) {
                entry.getKey().removeObserver(this);
                it.remove();
            } else {
                entry.getValue().removeIf(e -> !newDepends.contains(e));
            }

        }

        cacheValueMap.clear();
        flagged.clear();

        baseDirty = false;
    }

    private boolean updateFromScratch(Collection<E> newCollection) {
        for (Map.Entry<Observable<Boolean>, Set<E>> entry : dependsOn.entrySet()) {
            Observable<Boolean> observable = entry.getKey();
            boolean value = observable.get();
            if (value)
                newCollection.addAll(entry.getValue());


            cacheValueMap.put(observable, value);
        }

        return true;
    }

    private boolean updateElements(Set<E> elements, Collection<E> newCollection, Observable<Boolean> observable) {
        boolean hasChange = false;

        boolean value = observable.get();
        for (E element : elements) {
            boolean contains = newCollection.contains(element);

            if (value) {
                if (!contains) {
                    newCollection.add(element);
                    hasChange = true;
                }
            } else {
                if (contains) {
                    newCollection.remove(element);
                    hasChange = true;
                }
            }
        }

        return hasChange;
    }

    private boolean updateFlagged(Collection<E> newCollection) {
        boolean hasChange = false;
        newCollection.addAll(cachedCollection);

        for (Observable<Boolean> observable : flagged) {
            Set<E> elements = dependsOn.get(observable);
            hasChange |= updateElements(elements, newCollection, observable);
        }

        flagged.clear();
        return hasChange;
    }
    
    public C get() {
        if (!baseDirty && flagged.isEmpty())
            return cachedCollection;


        if (baseDirty)
            checkAndUpdateBase();

        boolean hasChange = false;
        Collection<E> newCollection = constructCollection.apply(dependsOn.size());
        if (cacheValueMap.isEmpty()) {
            hasChange = updateFromScratch(newCollection);
        } else if (!flagged.isEmpty()) {
            hasChange = updateFlagged(newCollection);
        }

        if (!hasChange)
            return cachedCollection;


        C result = collectionMapper.apply(newCollection);
        cachedCollection = result;

        return result;
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
