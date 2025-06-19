package net.apartium.cocoabeans.spigot.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import net.apartium.cocoabeans.spigot.NMSUtils;
import net.apartium.cocoabeans.spigot.SpigotServerCache;
import org.bukkit.craftbukkit.SpigotServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NMSUtilsTest {

    SpigotServerMock server;
    MockPlugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock(new SpigotServerMock());
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        SpigotServerCache.flush();
        server = null;
        plugin = null;
    }

    @Test
    void testFormatNMS1_20() {
        assertEquals("net.minecraft.server.players.PlayerList",
                NMSUtils.formatNMS("players.PlayerList"));
    }

    @Test
    void testFormatNMSWithFullPath1_20() {
        assertEquals("net.minecraft.server.players.PlayerList",
                NMSUtils.formatNMS("net.minecraft.server.players.PlayerList"));
    }

    @Test
    void testFormatNMSThrowsException() {
        MockBukkit.unmock();
        MockBukkit.mock();
        assertThrows(RuntimeException.class, () -> NMSUtils.formatNMS("players.PlayerList"),
                "Could not find method getHandle of class be.seeseemelk.mockbukkit.ServerMock");
    }

    @Test
    void testFormatNMSWithNullPath() {
        assertThrows(IllegalArgumentException.class, () -> NMSUtils.formatNMS(null), "Path cannot be null or empty");
    }

    @Test
    void testFormatNMSWithEmptyPath() {
        assertThrows(IllegalArgumentException.class, () -> NMSUtils.formatNMS(""), "Path cannot be null or empty");
    }

    @Test
    void testFormatOBCNullPath() {
        assertThrows(IllegalArgumentException.class, () -> NMSUtils.formatOBC(null), "Path cannot be null or empty");
    }

    @Test
    void testFormatOBCEmptyPath() {
        assertThrows(IllegalArgumentException.class, () -> NMSUtils.formatOBC(""), "Path cannot be null or empty");
    }
}


