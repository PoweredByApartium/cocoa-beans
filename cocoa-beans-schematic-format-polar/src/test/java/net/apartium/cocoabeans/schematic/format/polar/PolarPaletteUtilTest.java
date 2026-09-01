package net.apartium.cocoabeans.schematic.format.polar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolarPaletteUtilTest {

    @Test
    void bitsToRepresent() {
        assertEquals(1, PolarPaletteUtil.bitsToRepresent(1));
        assertEquals(2, PolarPaletteUtil.bitsToRepresent(2));
        assertEquals(2, PolarPaletteUtil.bitsToRepresent(3));
        assertEquals(3, PolarPaletteUtil.bitsToRepresent(4));
        assertEquals(5, PolarPaletteUtil.bitsToRepresent(16));
        assertEquals(8, PolarPaletteUtil.bitsToRepresent(255));
    }

    @Test
    void bitsToRepresentInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PolarPaletteUtil.bitsToRepresent(0));
        assertThrows(IllegalArgumentException.class, () -> PolarPaletteUtil.bitsToRepresent(-1));
    }

    @Test
    void bitsPerEntry() {
        assertEquals(1, PolarPaletteUtil.bitsPerEntry(1));
        assertEquals(1, PolarPaletteUtil.bitsPerEntry(2));
        assertEquals(2, PolarPaletteUtil.bitsPerEntry(3));
        assertEquals(2, PolarPaletteUtil.bitsPerEntry(4));
        assertEquals(3, PolarPaletteUtil.bitsPerEntry(5));
        assertEquals(4, PolarPaletteUtil.bitsPerEntry(16));
    }

    @Test
    void packAndUnpackRoundTrip() {
        int[] values = {0, 1, 2, 3, 0, 1, 2, 3};
        int bpe = PolarPaletteUtil.bitsPerEntry(4);

        long[] packed = PolarPaletteUtil.pack(values, bpe);
        int[] unpacked = PolarPaletteUtil.unpack(packed, bpe, values.length);

        assertArrayEquals(values, unpacked);
    }

    @Test
    void packAndUnpackSingleEntry() {
        int[] values = new int[4096]; // all zeros
        int bpe = 1;

        long[] packed = PolarPaletteUtil.pack(values, bpe);
        int[] unpacked = PolarPaletteUtil.unpack(packed, bpe, values.length);

        assertArrayEquals(values, unpacked);
    }

    @Test
    void packAndUnpackLargePalette() {
        int[] values = new int[4096];
        for (int i = 0; i < values.length; i++)
            values[i] = i % 256;

        int bpe = PolarPaletteUtil.bitsPerEntry(256);

        long[] packed = PolarPaletteUtil.pack(values, bpe);
        int[] unpacked = PolarPaletteUtil.unpack(packed, bpe, values.length);

        assertArrayEquals(values, unpacked);
    }

}