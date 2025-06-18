package net.apartium.cocoabeans.spigot.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.craftbukkit.v1_8_R3.SpigotServerMock_1_8;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NMSUtilsTest1_8 {

    ServerMock server;
    MockPlugin plugin;

    @BeforeEach
    void setUp() {

        server = MockBukkit.mock(new SpigotServerMock_1_8());
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        server = null;
        plugin = null;
    }

    @Test
    void testFormatOBC() {
        assertEquals("org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper",
                NMSUtils.formatOBC("command.VanillaCommandWrapper"));
    }

    @Test
    void testFormatNMS1_8() {
        assertEquals("net.minecraft.server.v1_8_R3.PlayerList",
                NMSUtils.fixNMSFQDNForNonMappedFormat("players.PlayerList"));
    }
}
