package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

@ApiStatus.Internal
/* package-private */ class ListFlatMapEachObservable<F, E> extends FlatMapElementObservable<F, E, List<E>> implements DerivedListObservable<E> {

    public ListFlatMapEachObservable(Observable<? extends Collection<F>> base, Function<F, Observable<E>> mapper, Function<Collection<E>, List<E>> collectionMapper, IntFunction<? extends Collection<E>> constructCollection) {
        super(base, mapper, collectionMapper, constructCollection);
    }
}
