package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Queue;

/**
 * Represents an observable queue that allows for observing changes to the underlying
 * {@link Queue} collection. This interface extends {@code CollectionObservable} and provides
 * standard queue-specific operations such as adding, offering, removing, and peeking elements.
 *
 * @param <E> the type of elements in this collection
 * @param <C> the type of the underlying queue collection
 * @see CollectionObservable
 */
@ApiStatus.AvailableSince("0.0.50")
public interface QueueObservable<E, C extends Queue<E>> extends CollectionObservable<E, C> {

    /**
     * Adds the specified element to the queue if it is possible to do so immediately
     * without violating capacity restrictions.
     *
     * @param element the element to be added to the queue
     * @return {@code true} if the element was successfully added, otherwise {@code false}
     */
    boolean add(E element);

    /**
     * Offers the specified element for insertion into the queue if it is possible to do so
     * immediately without violating capacity restrictions.
     *
     * @param element the element to be added to the queue
     * @return {@code true} if the element was successfully added, otherwise {@code false}
     */
    boolean offer(E element);

    /**
     * Removes the head of the queue.
     *
     * @return the element that was removed from the head of the queue
     * @throws java.util.NoSuchElementException if the queue is empty
     */
    E remove();

    /**
     * Retrieves and removes the head of the queue, or returns {@code null} if the queue is empty.
     *
     * @return the head of the queue, or {@code null} if the queue is empty
     */
    E poll();

    /**
     * Retrieves, but does not remove, the head of the queue. This method throws an exception
     * if the queue is empty.
     *
     * @return the head of the queue
     * @throws java.util.NoSuchElementException if the queue is empty
     */
    E element();

    /**
     * Retrieves, but does not remove, the head of the queue, or returns {@code null} if the queue is empty.
     *
     * @return the head of the queue, or {@code null} if the queue is empty
     */
    E peek();

}
