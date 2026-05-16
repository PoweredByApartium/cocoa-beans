package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ class SortedListObservable<E, T> implements DerivedListObservable<E>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private final Observable<? extends Collection<E>> base;
    private final Function<E, Observable<T>> mapper;
    private final Observable<Comparator<? super T>> comparator;
    private final ObservableCollectionType<E, ? extends List<E>, ListObservable<E>> type;

    private final Map<E, Observable<T>> innerByElement = new IdentityHashMap<>();
    private final Map<Observable<T>, Set<E>> dependsOn = new IdentityHashMap<>();
    private final Map<Observable<T>, T> cachedKeys = new IdentityHashMap<>();
    private final Set<Observable<T>> flagged = Collections.newSetFromMap(new IdentityHashMap<>());
    private Comparator<? super T> comparatorCache;

    private boolean baseDirty = true;
    private boolean innerDirty = true;
    private boolean comparatorDirty = true;

    private Collection<E> lastSource;
    private List<E> cachedCollection;

    private final Observable<Integer> size;

    public SortedListObservable(
            Observable<? extends Collection<E>> base,
            Function<E, Observable<T>> mapper,
            Observable<Comparator<? super T>> comparator,
            ObservableCollectionType<E, ? extends List<E>, ListObservable<E>> type
    ) {
        this.base = base;
        this.base.observe(this);

        this.mapper = mapper;
        this.comparator = comparator;
        this.comparator.observe(this);
        this.type = type;

        this.size = this.map(Collection::size);
    }

    private boolean updateBase() {
        if (!baseDirty)
            return false;

        Collection<E> source = base.get();
        if (Objects.equals(source, lastSource)) {
            baseDirty = false;
            return false;
        }

        this.lastSource = source;

        Set<E> keepElements = Collections.newSetFromMap(new IdentityHashMap<>());
        keepElements.addAll(source);

        Iterator<Map.Entry<E, Observable<T>>> elementIterator = innerByElement.entrySet().iterator();
        while (elementIterator.hasNext()) {
            Map.Entry<E, Observable<T>> entry = elementIterator.next();

            if (keepElements.contains(entry.getKey()))
                continue;

            removeInnerDependency(entry.getKey(), entry.getValue());
            elementIterator.remove();
        }

        for (E element : source) {
            if (innerByElement.containsKey(element))
                continue;

            Observable<T> inner = mapper.apply(element);
            innerByElement.put(element, inner);

            if (inner != null) {
                Set<E> elements = dependsOn.computeIfAbsent(inner, key -> {
                    key.observe(this);
                    return Collections.newSetFromMap(new IdentityHashMap<>());
                });

                elements.add(element);
            }
        }

        baseDirty = false;
        innerDirty = true;
        return true;
    }

    private void removeInnerDependency(E element, Observable<T> inner) {
        if (inner == null)
            return;

        Set<E> elements = dependsOn.get(inner);
        elements.remove(element);

        if (!elements.isEmpty())
            return;

        inner.removeObserver(this);
        dependsOn.remove(inner);
        cachedKeys.remove(inner);
        flagged.remove(inner);
    }

    @Override
    public List<E> get() {
        boolean dirty = updateBase();
        if (comparatorDirty) {
            Comparator<? super T> newComparator = comparator.get();
            if (newComparator != comparatorCache) {
                comparatorCache = newComparator;
                dirty = true;
            }

            comparatorDirty = false;
        }

        if (!dirty && !innerDirty)
            return cachedCollection;

        for (Observable<T> observable : flagged) {
            T newValue = observable.get();
            T prevValue = cachedKeys.put(observable, newValue);

            if (!Objects.equals(newValue, prevValue))
                dirty = true;
        }

        flagged.clear();
        if (!dirty)
            return cachedCollection;

        for (Observable<T> inner : dependsOn.keySet())
            cachedKeys.computeIfAbsent(inner, Observable::get);

        List<E> working = new ArrayList<>(lastSource);

        if (!isSortedByKey(working))
            working.sort((a, b) -> comparatorCache.compare(keyOf(a), keyOf(b)));

        cachedCollection = type.snapshot(working);
        innerDirty = false;

        return cachedCollection;
    }

    private boolean isSortedByKey(List<E> list) {
        if (list.size() < 2)
            return true;

        T prev = keyOf(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            T current = keyOf(list.get(i));

            if (comparatorCache.compare(prev, current) > 0)
                return false;

            prev = current;
        }

        return true;
    }

    private T keyOf(E element) {
        Observable<T> inner = innerByElement.get(element);
        if (inner == null)
            return null;

        return cachedKeys.get(inner);
    }

    protected void notifyObservers() {
        for (Observer observer : Set.copyOf(observers))
            observer.flagAsDirty(this);
    }

    @Override
        @SuppressWarnings("unchecked")
    public void flagAsDirty(Observable<?> observable) {
        if (observable == base) {
            baseDirty = true;
            notifyObservers();
            return;
        }

        if (observable == comparator) {
            comparatorDirty = true;
            notifyObservers();
            return;
        }

        if (dependsOn.containsKey(observable)) {
            flagged.add((Observable<T>) observable);
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

    @Override public boolean add(E element) { throw new UnsupportedOperationException("SortedListObservable does not support adding elements"); }

    @Override public boolean remove(E element) { throw new UnsupportedOperationException("SortedListObservable does not support removing elements"); }

    @Override public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("SortedListObservable does not support adding elements"); }

    @Override public boolean removeAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("SortedListObservable does not support removing elements"); }

    @Override public boolean removeIf(Predicate<? super E> filter) { throw new UnsupportedOperationException("SortedListObservable does not support removing elements"); }

    @Override public boolean retainAll(Collection<? extends E> collection) { throw new UnsupportedOperationException("SortedListObservable does not support removing elements"); }

    @Override public void clear() { throw new UnsupportedOperationException("SortedListObservable does not support removing elements"); }

    @Override
    public Observable<Integer> size() {
        return size;
    }

    @Override
    public Function<Collection<E>, List<E>> collectionMapper() {
        return type::snapshot;
    }

    @Override
    public IntFunction<? extends Collection<E>> constructCollection() {
        return type::collection;
    }

}
