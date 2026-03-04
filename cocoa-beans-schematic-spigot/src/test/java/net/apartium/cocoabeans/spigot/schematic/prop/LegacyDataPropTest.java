package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyDataPropTest {

    private static final NamespacedKey LADDER = new NamespacedKey("minecraft", "ladder");

    @Test
    void value() {
        assertEquals((byte) 2,         new LegacyDataProp((byte) 2).value());
        assertEquals((byte) 0,         new LegacyDataProp((byte) 0).value());
        assertEquals((byte) -1,        new LegacyDataProp((byte) -1).value());
        assertEquals(Byte.MIN_VALUE,   new LegacyDataProp(Byte.MIN_VALUE).value());
        assertEquals(Byte.MAX_VALUE,   new LegacyDataProp(Byte.MAX_VALUE).value());
    }

    @Test
    void rotateNonMinecraftNamespaceReturnsSelf() {
        LegacyDataProp prop = new LegacyDataProp((byte) 2);
        NamespacedKey key = new NamespacedKey("custom", "ladder");
        assertSame(prop, prop.rotate(key, 0));
        assertSame(prop, prop.rotate(key, 90));
        assertSame(prop, prop.rotate(key, 180));
        assertSame(prop, prop.rotate(key, 270));
    }

    @Test
    void rotateNonLadderMinecraftKeyReturnsSelf() {
        LegacyDataProp prop = new LegacyDataProp((byte) 2);
        assertSame(prop, prop.rotate(new NamespacedKey("minecraft", "stone"),  90));
        assertSame(prop, prop.rotate(new NamespacedKey("minecraft", "grass"),  180));
        assertSame(prop, prop.rotate(new NamespacedKey("minecraft", "stairs"), 270));
    }

    @Test
    void rotateLadderAllValuesZeroDegreesReturnSelf() {
        for (byte b : new byte[]{2, 3, 4, 5, 0, 1}) {
            LegacyDataProp prop = new LegacyDataProp(b);
            assertSame(prop, prop.rotate(LADDER, 0), "value=" + b);
        }
    }

    @Test
    void rotateLadderFromNorth() {
        assertEquals((byte) 5, new LegacyDataProp((byte) 2).rotate(LADDER, 90).value());
        assertEquals((byte) 3, new LegacyDataProp((byte) 2).rotate(LADDER, 180).value());
        assertEquals((byte) 4, new LegacyDataProp((byte) 2).rotate(LADDER, 270).value());
    }

    @Test
    void rotateLadderFromSouth() {
        assertEquals((byte) 4, new LegacyDataProp((byte) 3).rotate(LADDER, 90).value());
        assertEquals((byte) 2, new LegacyDataProp((byte) 3).rotate(LADDER, 180).value());
        assertEquals((byte) 5, new LegacyDataProp((byte) 3).rotate(LADDER, 270).value());
    }

    @Test
    void rotateLadderFromWest() {
        assertEquals((byte) 2, new LegacyDataProp((byte) 4).rotate(LADDER, 90).value());
        assertEquals((byte) 5, new LegacyDataProp((byte) 4).rotate(LADDER, 180).value());
        assertEquals((byte) 3, new LegacyDataProp((byte) 4).rotate(LADDER, 270).value());
    }

    @Test
    void rotateLadderFromEast() {
        assertEquals((byte) 3, new LegacyDataProp((byte) 5).rotate(LADDER, 90).value());
        assertEquals((byte) 4, new LegacyDataProp((byte) 5).rotate(LADDER, 180).value());
        assertEquals((byte) 2, new LegacyDataProp((byte) 5).rotate(LADDER, 270).value());
    }

    @Test
    void rotateLadderDefaultValuesBehaveLikeNorth() {
        for (byte b : new byte[]{0, 1, 6, 7}) {
            assertEquals((byte) 5, new LegacyDataProp(b).rotate(LADDER, 90).value(),  "value=" + b + ", 90°");
            assertEquals((byte) 3, new LegacyDataProp(b).rotate(LADDER, 180).value(), "value=" + b + ", 180°");
            assertEquals((byte) 4, new LegacyDataProp(b).rotate(LADDER, 270).value(), "value=" + b + ", 270°");
        }
    }

    @Test
    void rotateLadderKeyIsCaseInsensitive() {
        LegacyDataProp prop = new LegacyDataProp((byte) 2);
        assertEquals((byte) 5, prop.rotate(new NamespacedKey("minecraft", "LADDER"), 90).value());
        assertEquals((byte) 5, prop.rotate(new NamespacedKey("minecraft", "Ladder"), 90).value());
        assertEquals((byte) 5, prop.rotate(new NamespacedKey("minecraft", "laDDeR"), 90).value());
    }

    @Test
    void rotateReturnsLegacyDataPropInstance() {
        LegacyDataProp result = new LegacyDataProp((byte) 2).rotate(LADDER, 90);
        assertInstanceOf(LegacyDataProp.class, result);
    }

    @Test
    void fourRotationsOf90ReturnToOriginalValue() {
        for (byte b : new byte[]{2, 3, 4, 5}) {
            LegacyDataProp prop = new LegacyDataProp(b);
            LegacyDataProp r90  = prop.rotate(LADDER, 90);
            LegacyDataProp r180 = r90.rotate(LADDER, 90);
            LegacyDataProp r270 = r180.rotate(LADDER, 90);
            LegacyDataProp r360 = r270.rotate(LADDER, 90);
            assertEquals(prop.value(), r360.value(), "360° cycle failed for value=" + b);
        }
    }
}
