package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DirectionalFacePropTest {

    private static final NamespacedKey TYPE = new NamespacedKey("test", "type");

    @Test
    void flipXFlipsEastWest() {
        DirectionalFaceProp prop = new DirectionalFaceProp(BlockFace.EAST);
        assertEquals(new DirectionalFaceProp(BlockFace.WEST), prop.flip(TYPE, Axis.X));

        DirectionalFaceProp north = new DirectionalFaceProp(BlockFace.NORTH);
        assertSame(north, north.flip(TYPE, Axis.X));
    }

    @Test
    void flipYFlipsUpDown() {
        DirectionalFaceProp prop = new DirectionalFaceProp(BlockFace.UP);
        assertEquals(new DirectionalFaceProp(BlockFace.DOWN), prop.flip(TYPE, Axis.Y));

        DirectionalFaceProp south = new DirectionalFaceProp(BlockFace.SOUTH);
        assertSame(south, south.flip(TYPE, Axis.Y));
    }

    @Test
    void flipZFlipsNorthSouth() {
        DirectionalFaceProp prop = new DirectionalFaceProp(BlockFace.NORTH);
        assertEquals(new DirectionalFaceProp(BlockFace.SOUTH), prop.flip(TYPE, Axis.Z));

        DirectionalFaceProp east = new DirectionalFaceProp(BlockFace.EAST);
        assertSame(east, east.flip(TYPE, Axis.Z));
    }

    @Test
    void rotateCardinalDirections() {
        DirectionalFaceProp prop = new DirectionalFaceProp(BlockFace.NORTH);
        assertEquals(new DirectionalFaceProp(BlockFace.WEST), prop.rotate(TYPE, 90));
        assertEquals(new DirectionalFaceProp(BlockFace.EAST), prop.rotate(TYPE, 270));
        assertEquals(new DirectionalFaceProp(BlockFace.SOUTH), prop.rotate(TYPE, 180));
    }

    @Test
    void rotateNonRotatableOrUnsupportedDegreesReturnsSame() {
        DirectionalFaceProp up = new DirectionalFaceProp(BlockFace.UP);
        assertSame(up, up.rotate(TYPE, 90));

        DirectionalFaceProp north = new DirectionalFaceProp(BlockFace.NORTH);
        assertSame(north, north.rotate(TYPE, 45));
    }
}
