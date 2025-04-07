package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * Represents an observable containing multiple unique values
 * @param <E> element type
 * @see CollectionObservable
 * @see Observable
 */
@ApiStatus.AvailableSince("0.0.39")
public interface SetObservable<E> extends CollectionObservable<E, Set<E>> {

}
