package net.apartium.cocoabeans.scoreboard;

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
