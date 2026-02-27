package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.format.IntPropFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntPropFormatTest {

    @Test
    void serialize() {
        IntPropFormat format = new IntPropFormat();

        int[] cases = {
                0,
                1,
                -1,
                123456789,
                -987654321,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE
        };

        for (int v : cases) {
            byte[] encoded = format.encode(new IntBlockProp(v));
            assertEquals(4, encoded.length);

            assertEquals((byte) ((v >> 24) & 0xFF), encoded[0]);
            assertEquals((byte) ((v >> 16) & 0xFF), encoded[1]);
            assertEquals((byte) ((v >> 8) & 0xFF), encoded[2]);
            assertEquals((byte) (v & 0xFF), encoded[3]);
        }
    }

    @Test
    void deserialize() {
        IntPropFormat format = new IntPropFormat();

        int[] cases = {
                0,
                1,
                -1,
                123456789,
                -987654321,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE
        };

        for (int v : cases) {
            byte[] data = new byte[] {
                    (byte) ((v >> 24) & 0xFF),
                    (byte) ((v >> 16) & 0xFF),
                    (byte) ((v >> 8) & 0xFF),
                    (byte) (v & 0xFF),
            };

            assertEquals(v, format.decode(data).value());
        }
    }

    @Test
    void roundtripMany() {
        IntPropFormat format = new IntPropFormat();

        for (int i = 0; i < 10_000; i++) {
            int v = (i * 1103515245) ^ (i >>> 3) ^ 0xA5A5A5A5;

            byte[] encoded = format.encode(new IntBlockProp(v));
            int decoded = format.decode(encoded).value();

            assertEquals(v, decoded);
        }
    }

    @Test
    void failedSerialize() {
        IntPropFormat format = new IntPropFormat();

        assertThrows(IllegalArgumentException.class, () -> format.encode(new ByteBlockProp((byte) 1)));

        BlockProp<?> nullProp = () -> null;
        assertThrows(NullPointerException.class, () -> format.encode(nullProp));
    }

    @Test
    void failedDeserialize() {
        IntPropFormat format = new IntPropFormat();

        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[1]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[2]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[3]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[5]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[1024]));
    }
}