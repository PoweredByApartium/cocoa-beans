package net.apartium.cocoabeans.scoreboard;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.39")
public enum ObjectiveRenderType {
    /**
     * Display numerical score
     */
    INTEGER(0),

    /**
     * Display hearts, only supported on player list
     */
    HEARTS(1);

    private final int id;

    ObjectiveRenderType(int id) {
        this.id = id;
    }

    /**
     * Protocol id of the display type
     * @return Protocol id of the display type
     */
    public int getId() {
        return id;
    }
}
