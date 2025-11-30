package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.spigot.schematic.prop.RedstoneWireConnectionsProp;
import net.apartium.cocoabeans.spigot.schematic.prop.WallHeightsProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.Wall;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class WallHeightsPropFormatTest {

    @Test
    void emptyTest() {
        WallHeightsPropFormat format = WallHeightsPropFormat.INSTANCE;

        WallHeightsProp prop = new WallHeightsProp(Map.of());

        byte[] encode = format.encode(prop);

        WallHeightsProp otherProp = (WallHeightsProp) format.decode(encode);
        assertEqualProp(prop, otherProp);
    }


    @Test
    void simpleTest() {
        WallHeightsPropFormat format = WallHeightsPropFormat.INSTANCE;

        WallHeightsProp prop = new WallHeightsProp(Map.of(
                BlockFace.EAST, Wall.Height.TALL,
                BlockFace.SOUTH, Wall.Height.LOW
                ));

        byte[] encode = format.encode(prop);

        WallHeightsProp otherProp = (WallHeightsProp) format.decode(encode);
        assertEqualProp(prop, otherProp);
    }

    private static void assertEqualProp(WallHeightsProp prop, WallHeightsProp otherProp) {
        assertEquals(prop.value().size(), otherProp.value().size());
        prop.value().forEach((face, height) -> assertEquals(height, otherProp.value().get(face)));
    }

    @Test
    void badValue() {
        WallHeightsPropFormat format = WallHeightsPropFormat.INSTANCE;

        assertThrowsExactly(NullPointerException.class, () -> format.encode(() -> null));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> List.of("xD", true, 1, "wow")));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> Map.of("xD", true, 1, "wow")));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> Map.of(BlockFace.NORTH, true, BlockFace.WEST, "wow")));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> Map.of(BlockFace.NORTH, Wall.Height.TALL, BlockFace.WEST, "wow")));
    }


}
