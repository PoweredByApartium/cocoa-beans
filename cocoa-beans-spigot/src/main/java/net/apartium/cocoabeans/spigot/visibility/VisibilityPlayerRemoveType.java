package net.apartium.cocoabeans.spigot.visibility;

/**
 * Sets the listener behavior when a player quits the game
 */
public enum VisibilityPlayerRemoveType {

    /**
     * Don't remove the player from in-memory state when they quit
     * When the player will re-join the visibility policies will be automatically applied from the same group, but it can cause a memory leak
     */
    NEVER,

    /**
     * Remove the player from in memory state when they quit
     */
    ON_LEAVE
}