package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

@ApiStatus.Internal
/* package-private */ class MappedSetObservable<F, E> extends MapElementObservable<F, E, Set<E>> implements DerivedSetObservable<E> {

    public MappedSetObservable(Observable<? extends Collection<F>> base, Function<F, E> mapper, Function<Collection<E>, Set<E>> collectionMapper, IntFunction<? extends Collection<E>> constructCollection) {
        super(base, mapper, collectionMapper, constructCollection);
    }
}
