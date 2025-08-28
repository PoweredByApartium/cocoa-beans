package net.apartium.cocoabeans.scoreboard;

import org.jetbrains.annotations.ApiStatus;

/**
 * Display slot to put scoreboard
 */
@ApiStatus.AvailableSince("0.0.39")
public enum DisplaySlot {
    LIST(0),
    SIDEBAR(1),
    BELOW_NAME(2);

    private final int id;

    DisplaySlot(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
