package net.apartium.cocoabeans.schematic.format.polar;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
final class PolarPaletteUtil {

    private PolarPaletteUtil() {}

    static int bitsToRepresent(int n) {
        if (n < 1)
            throw new IllegalArgumentException("n must be greater than 0");

        return Integer.SIZE - Integer.numberOfLeadingZeros(n);
    }

    static int bitsPerEntry(int paletteSize) {
        return Math.max(1, (int) Math.ceil(Math.log(paletteSize) / Math.log(2)));
    }

    static long[] pack(int[] values, int bitsPerEntry) {
        int intsPerLong = (int) Math.floor(64.0 / bitsPerEntry);
        long[] longs = new long[(int) Math.ceil(values.length / (double) intsPerLong)];

        long mask = (1L << bitsPerEntry) - 1L;
        for (int i = 0; i < longs.length; i++) {
            for (int intIndex = 0; intIndex < intsPerLong; intIndex++) {
                int bitIndex = intIndex * bitsPerEntry;
                int intActualIndex = intIndex + i * intsPerLong;
                if (intActualIndex < values.length) {
                    longs[i] |= (values[intActualIndex] & mask) << bitIndex;
                }
            }
        }
        return longs;
    }

    static int[] unpack(long[] packed, int bitsPerEntry, int count) {
        int[] out = new int[count];
        int intsPerLong = (int) Math.ceil(64.0 / bitsPerEntry);

        long mask = (1L << bitsPerEntry) - 1L;
        for (int i = 0; i < count; i++) {
            int longIndex = i / intsPerLong;
            int subIndex = i % intsPerLong;

            out[i] = (int) ((packed[longIndex] >>> (bitsPerEntry * subIndex)) & mask);
        }
        return out;
    }

}