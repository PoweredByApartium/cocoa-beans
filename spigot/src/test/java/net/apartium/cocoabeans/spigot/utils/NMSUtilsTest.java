package net.apartium.cocoabeans.spigot.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import org.bukkit.craftbukkit.SpigotServerMock;
import org.bukkit.craftbukkit_more_args.SpigotServerMockMoreArgs;
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
    void testFormatNMSShouldReturnUnknownVersion() {
        MockBukkit.unmock();
        MockBukkit.mock(new SpigotServerMockMoreArgs());

        assertEquals("net.minecraft.server.players.PlayerList",
                NMSUtils.formatNMS("players.PlayerList"));
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


