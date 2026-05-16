package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

@ApiStatus.Internal
/* package-private */ class FilteredListObservable<E> extends FilterObservable<E, List<E>> implements DerivedListObservable<E> {

    public FilteredListObservable(Observable<List<E>> base, Function<E, Observable<Boolean>> filter, Function<Collection<E>, List<E>> collectionMapper, IntFunction<? extends Collection<E>> constructCollection) {
        super(base, filter, collectionMapper, constructCollection);
    }
}
