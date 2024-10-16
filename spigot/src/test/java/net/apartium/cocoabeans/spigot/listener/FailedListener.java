package net.apartium.cocoabeans.spigot.listener;

import org.bukkit.event.Listener;

import static org.junit.jupiter.api.Assertions.fail;

public class FailedListener implements Listener {

    public FailedListener() {
        throw new RuntimeException();
    }

}
