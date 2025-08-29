package net.apartium.cocoabeans.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.apartium.cocoabeans.spigot.fixture.CocoaPlayerMock;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

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

    public CocoaPlayerMock addPlayer(String name) {
        return addPlayer(UUID.randomUUID(), name);
    }

    public CocoaPlayerMock addPlayer(UUID uuid, String name) {
        CocoaPlayerMock playerMock = new CocoaPlayerMock(server, name, uuid);
        server.addPlayer(playerMock);
        return playerMock;
    }
}
