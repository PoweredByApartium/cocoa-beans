package net.apartium.cocoabeans.spigot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CocoaBeansTest extends SpigotTestBase {

    @Test
    void testPluginLoads() {
        assertNotNull(plugin);
    }

}
