package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

@ApiStatus.Internal
/* package-private */ class FilteredSetObservable<E> extends FilterObservable<E, Set<E>> implements DerivedSetObservable<E> {

    public FilteredSetObservable(Observable<Set<E>> base, Function<E, Observable<Boolean>> filter, Function<Collection<E>, Set<E>> copyOf, IntFunction<? extends Collection<E>> createInitSet) {
        super(base, filter, copyOf, createInitSet);
    }
}
