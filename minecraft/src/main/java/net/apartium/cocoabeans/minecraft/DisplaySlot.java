package net.apartium.cocoabeans.minecraft;

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
