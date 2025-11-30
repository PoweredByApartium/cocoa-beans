package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.spigot.schematic.prop.MultipleFacingFacesProp;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class MultipleFacingFacesPropFormatTest {

    @Test
    void emptyTest() {
        MultipleFacingFacesPropFormat format = MultipleFacingFacesPropFormat.INSTANCE;

        MultipleFacingFacesProp prop = new MultipleFacingFacesProp(Map.of());

        byte[] encode = format.encode(prop);

        MultipleFacingFacesProp otherProp = (MultipleFacingFacesProp) format.decode(encode);
        assertEqualProp(prop, otherProp);
    }


    @Test
    void simpleTest() {
        MultipleFacingFacesPropFormat format = MultipleFacingFacesPropFormat.INSTANCE;

        MultipleFacingFacesProp prop = new MultipleFacingFacesProp(Map.of(
                BlockFace.EAST, true,
                BlockFace.SOUTH, false
        ));

        byte[] encode = format.encode(prop);

        MultipleFacingFacesProp otherProp = (MultipleFacingFacesProp) format.decode(encode);
        assertEqualProp(prop, otherProp);
    }

    private static void assertEqualProp(MultipleFacingFacesProp prop, MultipleFacingFacesProp otherProp) {
        assertEquals(prop.value().size(), otherProp.value().size());
        prop.value().forEach((face, valueAsBoolean) -> assertEquals(valueAsBoolean, otherProp.value().get(face)));
    }

    @Test
    void badValue() {
        MultipleFacingFacesPropFormat format = MultipleFacingFacesPropFormat.INSTANCE;

        assertThrowsExactly(NullPointerException.class, () -> format.encode(() -> null));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> List.of("xD", true, 1, "wow")));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> Map.of("xD", true, 1, "wow")));
        assertThrowsExactly(IllegalArgumentException.class, () -> format.encode(() -> Map.of(BlockFace.NORTH, true, BlockFace.WEST, "wow")));
    }


}
