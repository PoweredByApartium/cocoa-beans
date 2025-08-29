package net.apartium.cocoabeans.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class CocoaBeansTestBase {

    protected ServerMock server;
    protected JavaPlugin plugin;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
