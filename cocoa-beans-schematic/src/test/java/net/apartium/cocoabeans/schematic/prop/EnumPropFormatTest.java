package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.format.EnumPropFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumPropFormatTest {

    private enum TestEnum {
        A, B, C
    }

    private record TestEnumProp(TestEnum value) implements BlockProp<TestEnum> {}

    private static final class TestEnumPropFormat extends EnumPropFormat<TestEnum> {
        TestEnumPropFormat() {
            super(TestEnum.class, TestEnum::values, TestEnumProp::new);
        }
    }

    @Test
    void roundtripEncodeDecode() {
        TestEnumPropFormat format = new TestEnumPropFormat();

        for (TestEnum e : TestEnum.values()) {
            byte[] encoded = format.encode(new TestEnumProp(e));
            assertNotNull(encoded);
            assertTrue(encoded.length > 0);

            BlockProp<TestEnum> decoded = format.decode(encoded);
            assertEquals(e, decoded.value());
        }
    }

    @Test
    void encodeRejectsWrongPropType() {
        TestEnumPropFormat format = new TestEnumPropFormat();

        IntBlockProp intProp = new IntBlockProp(1);
        ByteBlockProp byteProp = new ByteBlockProp((byte) 1);

        assertThrows(IllegalArgumentException.class, () -> format.encode(intProp));
        assertThrows(IllegalArgumentException.class, () -> format.encode(byteProp));
    }

    @Test
    void encodeRejectsNullValue() {
        TestEnumPropFormat format = new TestEnumPropFormat();

        BlockProp<?> nullProp = () -> null;

        assertThrows(IllegalArgumentException.class, () -> format.encode(nullProp));
        assertThrows(IllegalArgumentException.class, () -> format.encode(() -> "Hey!"));
    }

    @Test
    void decodeRejectsInvalidData() {
        TestEnumPropFormat format = new TestEnumPropFormat();

        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[] {0x7F}));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[] {(byte) 0xFF}));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[1024]));
    }
}