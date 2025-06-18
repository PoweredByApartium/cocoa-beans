package net.apartium.cocoabeans.spigot.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import net.apartium.cocoabeans.spigot.SpigotServerMock;
import org.bukkit.craftbukkit.v1_8_R3.SpigotServerMock_1_8;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NMSUtilsTest {

    SpigotServerMock server;
    MockPlugin plugin;

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testFixNMSFQDNForNonMappedFormat_1_20() {
        server = MockBukkit.mock(new SpigotServerMock());
        plugin = MockBukkit.createMockPlugin();

        server.getScheduler().performTicks(1000);

        assertEquals("net.minecraft.server.players.PlayerList",
                NMSUtils.fixNMSFQDNForNonMappedFormat("players.PlayerList"));
    }

    @Test
    void testFixNMSFQDNForNonMappedFormat() {
        server = MockBukkit.mock(new SpigotServerMock_1_8());
        plugin = MockBukkit.createMockPlugin();

        assertEquals("net.minecraft.server.v1_8_R3.players.PlayerList",
                NMSUtils.fixNMSFQDNForNonMappedFormat("players.PlayerList"));
    }

    @Test
    void testFormatOBC() {
        server = MockBukkit.mock(new SpigotServerMock_1_8());
        plugin = MockBukkit.createMockPlugin();

        assertEquals("org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper",
                NMSUtils.formatOBC("command.VanillaCommandWrapper"));
    }

}


