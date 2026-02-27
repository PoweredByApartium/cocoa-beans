package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.format.IntArrayPropFormat;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class IntArrayPropFormatTest {

    private static final class IntArrayBlockProp implements BlockProp<int[]> {
        private final int[] value;
        private IntArrayBlockProp(int[] value) { this.value = value; }
        @Override public int[] value() { return value; }
    }

    private static int readIntBE(byte[] arr, int byteOffset) {
        return (arr[byteOffset] & 0xFF) << 24
                | (arr[byteOffset + 1] & 0xFF) << 16
                | (arr[byteOffset + 2] & 0xFF) << 8
                | (arr[byteOffset + 3] & 0xFF);
    }

    @Test
    void serialize() {
        IntArrayPropFormat format = new IntArrayPropFormat(IntArrayBlockProp::new);

        int[][] cases = {
                {},
                {0},
                {1, 2, 3},
                {-1, 0, 1},
                {Integer.MIN_VALUE, Integer.MAX_VALUE, 123456789, -987654321}
        };

        for (int[] input : cases) {
            byte[] encoded = format.encode(new IntArrayBlockProp(input));

            assertEquals(4 + input.length * 4, encoded.length);

            assertEquals(input.length, readIntBE(encoded, 0));

            for (int i = 0; i < input.length; i++) {
                assertEquals(input[i], readIntBE(encoded, 4 + i * 4));
            }
        }
    }

    @Test
    void deserialize() {
        IntArrayPropFormat format = new IntArrayPropFormat(IntArrayBlockProp::new);

        int[] input = {Integer.MIN_VALUE, -1, 0, 1, 2, Integer.MAX_VALUE};

        byte[] encoded = format.encode(new IntArrayBlockProp(input));
        BlockProp<int[]> decoded = format.decode(encoded);

        assertArrayEquals(input, decoded.value());
    }

    @Test
    void roundtripMany() {
        IntArrayPropFormat format = new IntArrayPropFormat(IntArrayBlockProp::new);

        for (int len = 0; len <= 128; len++) {
            int[] arr = new int[len];
            for (int i = 0; i < len; i++) {
                arr[i] = (i * 1103515245) ^ (len * 12345);
            }

            byte[] encoded = format.encode(new IntArrayBlockProp(arr));
            int[] decoded = format.decode(encoded).value();

            assertArrayEquals(arr, decoded);
        }
    }

    @Test
    void failedSerialize() {
        IntArrayPropFormat format = new IntArrayPropFormat(IntArrayBlockProp::new);

        assertThrows(IllegalArgumentException.class, () -> format.encode(new IntBlockProp(1)));
        assertThrows(IllegalArgumentException.class, () -> format.encode(new ByteBlockProp((byte) 1)));

        BlockProp<?> nullProp = () -> null;
        assertThrows(NullPointerException.class, () -> format.encode(nullProp));
    }

    @Test
    void failedDeserializeTooShort() {
        IntArrayPropFormat format = new IntArrayPropFormat(IntArrayBlockProp::new);

        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[1]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[2]));
        assertThrows(IllegalArgumentException.class, () -> format.decode(new byte[3]));
    }

    @Test
    void failedDeserializeLengthBiggerThanPayload() {
        IntArrayPropFormat format = new IntArrayPropFormat(IntArrayBlockProp::new);

        byte[] bad = new byte[4 + 4];
        bad[0] = 0;
        bad[1] = 0;
        bad[2] = 0;
        bad[3] = 2;

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> format.decode(bad));
    }

    @Test
    void failedDeserializeNegativeLength() {
        IntArrayPropFormat format = new IntArrayPropFormat(IntArrayBlockProp::new);

        byte[] bad = new byte[4];
        bad[0] = (byte) 0xFF;
        bad[1] = (byte) 0xFF;
        bad[2] = (byte) 0xFF;
        bad[3] = (byte) 0xFF;

        assertThrows(NegativeArraySizeException.class, () -> format.decode(bad));
    }
}