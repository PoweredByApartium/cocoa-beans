package net.apartium.cocoabeans.spigot.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import net.apartium.cocoabeans.spigot.SpigotServerMock;
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
    void testFixNMSFQDNForNonMappedFormat_1_20() {
        assertEquals("net.minecraft.server.players.PlayerList",
                NMSUtils.fixNMSFQDNForNonMappedFormat("players.PlayerList"));
    }

    @Test
    void testShouldUsePackageWithVersionGetHandleNotFound() {
        tearDown();
        MockBukkit.mock();

        assertThrows(ExceptionInInitializerError.class, () -> NMSUtils.formatOBC("command.VanillaCommandWrapper"),
                "Could not find method getHandle of class be.seeseemelk.mockbukkit.ServerMock");
    }
}


