package net.apartium.cocoabeans.spigot.utils;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.spigot.SpigotTestBase;
import org.bukkit.metadata.FixedMetadataValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetadataUtilsTest extends SpigotTestBase {

    private PlayerMock playerForTest;

    @Override
    public void initialize() {
        playerForTest = server.addPlayer();
    }

    @Test
    void getMetadataValue() {
        playerForTest.setMetadata("test", new FixedMetadataValue(plugin, new TestRecord("shamen")));
        TestRecord test = MetadataUtils.getMetadataValue(playerForTest, "test", TestRecord.class, plugin).orElseThrow();

        assertEquals("shamen", test.test);
    }

    record TestRecord(String test) {
    }

}
