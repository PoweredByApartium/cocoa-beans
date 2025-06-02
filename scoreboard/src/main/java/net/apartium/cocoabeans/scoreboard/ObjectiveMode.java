package net.apartium.cocoabeans.scoreboard;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.39")
public enum ObjectiveMode {
    CREATE(0),
    REMOVE(1),
    UPDATE(2);

    private final int id;

    ObjectiveMode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
