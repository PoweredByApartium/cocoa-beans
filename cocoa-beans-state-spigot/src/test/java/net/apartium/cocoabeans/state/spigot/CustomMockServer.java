package net.apartium.cocoabeans.state.spigot;

import be.seeseemelk.mockbukkit.ServerMock;

public class CustomMockServer extends ServerMock {

    private int currentTick = 0;

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    @Override
    public int getCurrentTick() {
        return currentTick;
    }
}
