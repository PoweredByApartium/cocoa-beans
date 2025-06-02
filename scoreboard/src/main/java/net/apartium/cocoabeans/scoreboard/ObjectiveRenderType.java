package net.apartium.cocoabeans.scoreboard;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.39")
public enum ObjectiveRenderType {
    INTEGER(0),
    HEARTS(1);

    private final int id;

    ObjectiveRenderType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
