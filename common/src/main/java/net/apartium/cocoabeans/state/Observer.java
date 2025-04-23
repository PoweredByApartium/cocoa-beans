package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a listener in the state system
 * @see Watcher
 */
@ApiStatus.AvailableSince("0.0.39")
public interface Observer {

    /**
     * Flag listener as dirty
     * @param observable marking observable
     */
    @ApiStatus.OverrideOnly
    void flagAsDirty(Observable<?> observable);

}
