package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.format.BooleanPropFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BooleanPropTest {

    @Test
    void serialize() {
        BooleanPropFormat format = new BooleanPropFormat(BooleanBlockProp::new);

        BooleanBlockProp propOn = new BooleanBlockProp(true);
        BooleanBlockProp propOff = new BooleanBlockProp(false);

        byte[] encodeOn = format.encode(propOn);

        assertEquals(1, encodeOn.length);
        assertEquals((byte) 1, encodeOn[0]);

        byte[] encodeOff = format.encode(propOff);

        assertEquals(1, encodeOff.length);
        assertEquals((byte) 0, encodeOff[0]);
    }

    @Test
    void deserialize() {
        BooleanPropFormat format = new BooleanPropFormat(BooleanBlockProp::new);

        assertEquals(true, format.decode(new byte[]{1}).value());
        assertEquals(false, format.decode(new byte[]{0}).value());
    }

    @Test
    void failedSerialize() {
        BooleanPropFormat format = new BooleanPropFormat(BooleanBlockProp::new);

        IntBlockProp prop = new IntBlockProp(1);
        assertThrows(IllegalArgumentException.class, () -> format.encode(prop));
    }

    @Test
    void failedDeserialize() {
        BooleanPropFormat format = new BooleanPropFormat();

        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[2]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[3]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[4]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[1024]));
    }

}
