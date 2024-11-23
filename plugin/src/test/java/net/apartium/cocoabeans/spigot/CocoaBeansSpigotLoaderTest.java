package net.apartium.cocoabeans.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CocoaBeansSpigotLoaderTest {

    protected ServerMock server;

    protected CocoaBeansSpigotLoader plugin;

    @BeforeEach
    void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(CocoaBeansSpigotLoader.class);
    }

    @Test
    void testLoaded() {
        assertNotNull(plugin);
        assertTrue(plugin.isEnabled());
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

}
