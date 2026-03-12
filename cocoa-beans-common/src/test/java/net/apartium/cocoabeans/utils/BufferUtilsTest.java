package net.apartium.cocoabeans.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.CRC32;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BufferUtilsTest {

    private enum Color {
        RED,
        GREEN,
        BLUE
    }

    private enum Size {
        SMALL,
        LARGE
    }

    @Test
    void readAndWriteUnsignedIntegers() throws IOException {
        int u16 = 0xABCD;
        byte[] u16Bytes = BufferUtils.writeU16(u16);
        assertArrayEquals(new byte[] { (byte) 0xAB, (byte) 0xCD }, u16Bytes);
        assertEquals(u16, BufferUtils.readU16(dataInput(u16Bytes)));

        int u24 = 0xABCDEF;
        byte[] u24Bytes = BufferUtils.writeU24(u24);
        assertArrayEquals(new byte[] { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }, u24Bytes);
        assertEquals(u24, BufferUtils.readU24(dataInput(u24Bytes)));

        int u32 = 0x89ABCDEF;
        byte[] u32Bytes = BufferUtils.writeU32(u32);
        assertArrayEquals(new byte[] { (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }, u32Bytes);
        assertEquals(0x89ABCDEFL, BufferUtils.readU32(dataInput(u32Bytes)));

        long u64 = 0x0123456789ABCDEFL;
        byte[] u64Bytes = BufferUtils.writeU64(u64);
        assertArrayEquals(
                new byte[] {
                        0x01, 0x23, 0x45, 0x67,
                        (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
                },
                u64Bytes
        );
        assertEquals(u64, BufferUtils.readU64(dataInput(u64Bytes)));
    }

    @Test
    void readStringRoundTrip() throws IOException {
        String value = "hello-world";
        byte[] bytes = BufferUtils.writeString(value);
        assertEquals(value, BufferUtils.readString(dataInput(bytes)));

        DataInputStream directInput = dataInput("hi".getBytes(StandardCharsets.UTF_8));
        assertEquals("hi", BufferUtils.readString(directInput, 2));
    }

    @Test
    void readStringNegativeLengthThrows() {
        byte[] bytes = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        assertThrows(EOFException.class, () -> BufferUtils.readString(dataInput(bytes)));
    }

    @Test
    void writeStringAsListMatchesByteArray() {
        List<Byte> list = BufferUtils.writeStringAsList("abc");
        byte[] fromList = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            fromList[i] = list.get(i);
        }

        assertArrayEquals(BufferUtils.writeString("abc"), fromList);
    }

    @Test
    void uuidConversion() {
        UUID uuid = new UUID(0x0123456789ABCDEFL, 0x0FEDCBA987654321L);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        assertEquals(uuid, BufferUtils.toUUID(buffer.array()));
        assertThrows(IllegalArgumentException.class, () -> BufferUtils.toUUID(new byte[15]));
    }

    @Test
    void crc32MatchesJavaAndResets() {
        byte[] hello = "hello".getBytes(StandardCharsets.UTF_8);
        byte[] world = "world".getBytes(StandardCharsets.UTF_8);

        assertEquals(crc32(hello), BufferUtils.crc32(hello));
        assertEquals(crc32(world), BufferUtils.crc32(world));
    }

    @Test
    void toBooleanArrayReflectsBits() {
        boolean[] bits = BufferUtils.toBooleanArray(0b1011L);
        assertEquals(64, bits.length);
        assertTrue(bits[0]);
        assertTrue(bits[1]);
        assertFalse(bits[2]);
        assertTrue(bits[3]);
        assertFalse(bits[4]);

        boolean[] highBit = BufferUtils.toBooleanArray(1L << 63);
        assertTrue(highBit[63]);
        assertFalse(highBit[62]);
    }

    @Test
    void enumReadWriteAndFallback() throws IOException {
        byte[] bytes = BufferUtils.writeEnum(Color.GREEN);
        Color decoded = BufferUtils.readEnum(dataInput(bytes), Color.class, Color::values);
        assertEquals(Color.GREEN, decoded);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(output);
        out.write(BufferUtils.writeString("MISSING"));
        out.writeInt(2);

        Color fallback = BufferUtils.readEnum(dataInput(output.toByteArray()), Color.class, Color::values);
        assertEquals(Color.BLUE, fallback);
    }

    @Test
    void mapOfEnumBooleanRoundTrip() {
        Map<Color, Boolean> map = new LinkedHashMap<>();
        map.put(Color.RED, true);
        map.put(Color.BLUE, false);

        byte[] bytes = BufferUtils.writeMapOfEnumBoolean(map);
        Map<Color, Boolean> decoded = BufferUtils.readMapOfEnumBoolean(
                dataInput(bytes),
                Color.class,
                Color::values
        );

        assertEquals(map, decoded);

        Map<Color, Boolean> empty = BufferUtils.readMapOfEnumBoolean(
                dataInput(BufferUtils.writeMapOfEnumBoolean(Map.<Color, Boolean>of())),
                Color.class,
                Color::values
        );
        assertTrue(empty.isEmpty());
    }

    @Test
    void mapOfEnumsRoundTrip() {
        Map<Color, Size> map = new LinkedHashMap<>();
        map.put(Color.RED, Size.SMALL);
        map.put(Color.GREEN, Size.LARGE);

        byte[] bytes = BufferUtils.writeMapOfEnums(map);
        Map<Color, Size> decoded = BufferUtils.readMapOfEnums(
                dataInput(bytes),
                Color.class,
                Color::values,
                Size.class,
                Size::values
        );

        assertEquals(map, decoded);
    }

    @Test
    void occupancyMaskForListAndArray() {
        List<Integer> list = Arrays.asList(1, null, 2, null, 3, 4, null, null, 5);
        byte[] listMask = BufferUtils.createOccupancyMask(list);
        assertArrayEquals(new byte[] { (byte) 0x35, (byte) 0x01 }, listMask);

        String[] arr = new String[] { "A", "", null, "B" };
        byte[] arrayMask = BufferUtils.createOccupancyMask(arr, value -> value != null && !value.isEmpty());
        assertArrayEquals(new byte[] { (byte) 0x09 }, arrayMask);
    }

    private static DataInputStream dataInput(byte[] bytes) {
        return new DataInputStream(new ByteArrayInputStream(bytes));
    }

    private static long crc32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, 0, data.length);
        return crc32.getValue();
    }
}
