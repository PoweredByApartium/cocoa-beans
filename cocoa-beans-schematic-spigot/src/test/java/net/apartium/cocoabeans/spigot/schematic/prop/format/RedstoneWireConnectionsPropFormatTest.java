package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.spigot.schematic.prop.RedstoneWireConnectionsProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RedstoneWireConnectionsPropFormatTest {

    @Test
    void emptyTest() {
        RedstoneWireConnectionsPropFormat format = RedstoneWireConnectionsPropFormat.INSTANCE;

        RedstoneWireConnectionsProp prop = new RedstoneWireConnectionsProp(Map.of());

        byte[] encode = format.encode(prop);

        RedstoneWireConnectionsProp otherProp = (RedstoneWireConnectionsProp) format.decode(encode);
        assertEqualProp(prop, otherProp);
    }


    @Test
    void simpleTest() {
        RedstoneWireConnectionsPropFormat format = RedstoneWireConnectionsPropFormat.INSTANCE;

        RedstoneWireConnectionsProp prop = new RedstoneWireConnectionsProp(Map.of(
                BlockFace.EAST, RedstoneWire.Connection.SIDE,
                BlockFace.WEST, RedstoneWire.Connection.UP
        ));

        byte[] encode = format.encode(prop);

        RedstoneWireConnectionsProp otherProp = (RedstoneWireConnectionsProp) format.decode(encode);
        assertEqualProp(prop, otherProp);
    }

    private static void assertEqualProp(RedstoneWireConnectionsProp prop, RedstoneWireConnectionsProp otherProp) {
        assertEquals(prop.value().size(), otherProp.value().size());
        prop.value().forEach((face, connection) -> assertEquals(connection, otherProp.value().get(face)));
    }

    @Test
    void badValue() {
        RedstoneWireConnectionsPropFormat format = RedstoneWireConnectionsPropFormat.INSTANCE;

        assertThrowsExactly(NullPointerException.class, () -> format.encode(() -> null));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> List.of("xD", true, 1, "wow")));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> Map.of("xD", true, 1, "wow")));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> Map.of(BlockFace.NORTH, true, BlockFace.WEST, "wow")));
    }

}
