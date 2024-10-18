package net.apartium.cocoabeans.spigot.listener;

public class MyManager implements TheManager {

    private boolean hasBeenCreated = false;

    @Override
    public boolean hasBeenCreated() {
        return hasBeenCreated;
    }

    @Override
    public void updateHasBeenCreated() {
        hasBeenCreated = true;
    }
}
