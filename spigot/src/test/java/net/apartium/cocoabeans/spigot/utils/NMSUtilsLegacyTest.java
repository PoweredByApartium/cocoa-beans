package net.apartium.cocoabeans.spigot.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import net.apartium.cocoabeans.spigot.NMSUtils;
import net.apartium.cocoabeans.spigot.ServerInfoStore;
import org.bukkit.craftbukkit.v1_8_R3.SpigotLegacyServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NMSUtilsLegacyTest {

    ServerMock server;
    MockPlugin plugin;

    @BeforeEach
    void setUp() {
        ServerInfoStore.flush();
        server = MockBukkit.mock(new SpigotLegacyServerMock());
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        ServerInfoStore.flush();
        server = null;
        plugin = null;
    }

    @Test
    void testFormatOBC() {
        assertEquals("org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper",
                NMSUtils.formatOBC("command.VanillaCommandWrapper"));
    }

    @Test
    void testFormatOBCWithFullPathProvided() {
        assertEquals("org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper",
                NMSUtils.formatOBC("org.bukkit.craftbukkit.command.VanillaCommandWrapper"));
    }

    @Test
    void testFormatNMS1_8() {
        assertEquals("net.minecraft.server.v1_8_R3.PlayerList",
                NMSUtils.formatNMS("players.PlayerList"));
    }

    @Test
    void testFormatNMSWithFullPath1_8() {
        assertEquals("net.minecraft.server.v1_8_R3.PlayerList",
                NMSUtils.formatNMS("net.minecraft.server.players.PlayerList"));
    }
}
