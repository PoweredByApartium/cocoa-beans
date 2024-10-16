package net.apartium.cocoabeans.spigot.listener;

public class MyManager {

    private boolean hasBeenCreated = false;

    public boolean hasBeenCreated() {
        return hasBeenCreated;
    }

    public void updateHasBeenCreated() {
        hasBeenCreated = true;
    }
}
