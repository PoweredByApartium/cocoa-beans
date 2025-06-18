package net.apartium.cocoabeans.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class SpigotTestBase {

    protected ServerMock server;

    protected MockPlugin plugin;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock(new SpigotServerMock());
        plugin = MockBukkit.createMockPlugin();

        initialize();
    }

    public abstract void initialize();

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        server = null;
        plugin = null;
    }


}
