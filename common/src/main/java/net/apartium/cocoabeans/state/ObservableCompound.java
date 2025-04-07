package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.CollectionHelpers;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

@ApiStatus.Internal
public class ObservableCompound<T> implements Observable<T>, Observer {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    private boolean isDirty = true;
    private boolean first = true;
    private final Map<Observable<?>, Object> dependsOn = new LinkedHashMap<>();
    private final Function<List<?>, T> singularMapper;

    private T cachedValue;

    public ObservableCompound(Function<List<?>, T> singularMapper, List<Observable<?>> list) {
        this.singularMapper = singularMapper;

        for (Observable<?> state : list) {
            dependsOn.put(state, null);
            state.observe(this);
        }
    }

    @Override
    public T get() {
        if (!isDirty && !first)
            return cachedValue;

        boolean hadChange = false;
        List<Object> values = new ArrayList<>(dependsOn.size());
        for (Map.Entry<Observable<?>, Object> entry : dependsOn.entrySet()) {
            Object obj = entry.getKey().get();

            values.add(obj);
            if (!Objects.equals(obj, entry.getValue())) {
                if (!(obj instanceof Collection<?> collection)) {
                    hadChange = true;
                    dependsOn.put(entry.getKey(), obj);
                    continue;
                }

                if (!CollectionHelpers.equalsCollections(collection, (Collection<?>) entry.getValue())) {
                    hadChange = true;
                    dependsOn.put(entry.getKey(), obj);
                    continue;
                }
            }
        }

        if (!first && !hadChange) {
            isDirty = false;
            return cachedValue;
        }

        first = false;
        isDirty = false;
        T temp = singularMapper.apply(Collections.unmodifiableList(values));

        boolean hasChange = !Objects.equals(temp, cachedValue);
        cachedValue = temp;

        if (hasChange) {
            for (Observer observer : observers)
                observer.flagAsDirty(this);
        }

        return cachedValue;
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (!dependsOn.containsKey(observable))
            return;

        isDirty = true;

        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

}
