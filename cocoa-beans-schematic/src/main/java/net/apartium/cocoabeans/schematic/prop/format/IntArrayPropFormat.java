package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.0.46")
public record IntArrayPropFormat(Function<int[], BlockProp<int[]>> constructor) implements BlockPropFormat<int[]> {

    /**
     * {@inheritDoc}
     *
     * <p>The binary layout is: a 4-byte big-endian integer for the array length,
     * followed by that many 4-byte big-endian integers for the element values.</p>
     *
     * @throws IllegalArgumentException if {@code value} is fewer than 4 bytes
     */
    @Override
    public BlockProp<int[]> decode(byte[] value) {
        if (value.length < 4)
            throw new IllegalArgumentException("Array length must be at least 4 bytes");

        int length = readIntAt(value, 0);
        int[] array = new int[length];

        for (int i = 0; i < length; i++)
            array[i] = readIntAt(value, i + 1);

        return constructor.apply(array);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The binary layout is: a 4-byte big-endian integer for the array length,
     * followed by that many 4-byte big-endian integers for the element values.</p>
     *
     * @throws IllegalArgumentException if the prop value is not an {@code int[]}
     */
    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof int[] array))
            throw new IllegalArgumentException("Prop value isn't type of int[] instead: " + prop.value().getClass().getSimpleName());


        byte[] result = new byte[array.length * 4 + 4];
        writeIntAt(result, array.length, 0);
        for (int i = 0; i < array.length; i++)
            writeIntAt(result, array[i], i + 1);

        return result;
    }

    private int readIntAt(byte[] value, int offset) {
        return (value[offset * 4] & 0xFF) << 24
                | (value[offset * 4 + 1] & 0xFF) << 16
                | (value[offset * 4 + 2] & 0xFF) << 8
                | (value[offset * 4 + 3] & 0xFF);
    }

    private void writeIntAt(byte[] result, int num, int index) {
        result[index * 4] = (byte) ((num >> 24) & 0xFF);
        result[index * 4 + 1] = (byte) ((num >> 16) & 0xFF);
        result[index * 4 + 2] = (byte) ((num >> 8) & 0xFF);
        result[index * 4 + 3] = (byte) (num & 0xFF);
    }

}
