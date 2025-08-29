package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

/**
 * Retrieves the argument from the context
 * @param <T> the type of the argument
 */
@FunctionalInterface
@ApiStatus.AvailableSince("0.0.36")
public interface ArgumentIndex<T> {

    /**
     * Get the argument from the context
     * @param context command context
     * @return the argument
     */
    T get(ArgumentContext context);

}
