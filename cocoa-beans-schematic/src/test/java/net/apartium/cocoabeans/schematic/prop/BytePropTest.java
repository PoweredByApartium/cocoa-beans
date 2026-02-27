package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.format.ByteBlockPropFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BytePropTest {

    @Test
    void serialize() {
        ByteBlockPropFormat format = new ByteBlockPropFormat();

        for (byte i = -128; i < 127; i++) {
            byte[] encode = format.encode(new ByteBlockProp(i));
            assertEquals(1, encode.length);
            assertEquals(i, encode[0]);
        }
    }

    @Test
    void deserialize() {
        ByteBlockPropFormat format = new ByteBlockPropFormat();

        for (byte i = -128; i < 127; i++) {
            byte[] data = new byte[]{i};

            assertEquals(i, format.decode(data).value());
        }
    }

    @Test
    void failedSerialize() {
        ByteBlockPropFormat format = new ByteBlockPropFormat();

        IntBlockProp prop = new IntBlockProp(1);
        assertThrows(IllegalArgumentException.class, () -> format.encode(prop));
    }

    @Test
    void failedDeserialize() {
        ByteBlockPropFormat format = new ByteBlockPropFormat();

        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[2]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[3]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[4]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[1024]));
    }

}
