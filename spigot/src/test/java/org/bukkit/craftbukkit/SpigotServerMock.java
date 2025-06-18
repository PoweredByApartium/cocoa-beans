package org.bukkit.craftbukkit;

import be.seeseemelk.mockbukkit.ServerMock;

public class SpigotServerMock extends ServerMock {

    public MethodMock getHandle() {
        // empty
        return new MethodMock();
    }

    public class MethodMock {

    }
}