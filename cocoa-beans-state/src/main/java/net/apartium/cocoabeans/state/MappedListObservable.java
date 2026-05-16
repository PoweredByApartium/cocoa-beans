package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

@ApiStatus.Internal
/* package-private */ class MappedListObservable<F, E> extends MapElementObservable<F, E, List<E>> implements DerivedListObservable<E> {

    public MappedListObservable(Observable<? extends Collection<F>> base, Function<F, E> mapper, Function<Collection<E>, List<E>> collectionMapper, IntFunction<? extends Collection<E>> constructCollection) {
        super(base, mapper, collectionMapper, constructCollection);
    }
}
