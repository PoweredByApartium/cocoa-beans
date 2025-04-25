package net.apartium.cocoabeans.minecraft;

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
