package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;

import java.util.function.Function;

public record ArrayIntPropFormat(Function<int[], BlockProp<int[]>> constructor) implements BlockPropFormat<int[]> {

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

    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof int[] array))
            return null;


        byte[] result = new byte[array.length * 4 + 4];
        writeIntAt(result, 0, array.length);
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
