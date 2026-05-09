package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;

@ApiStatus.Internal
/* package-private */ interface DerivedListObservable<E> extends ListObservable<E> {

    @Override
    default void add(int index, E element) { throw ListChainHelpers.unsupportedAdd(); }

    @Override
    default E remove(int index) { throw ListChainHelpers.unsupportedRemove(); }

    @Override
    default void sort(Comparator<? super E> comparator) { throw ListChainHelpers.unsupportedSort(); }

}
