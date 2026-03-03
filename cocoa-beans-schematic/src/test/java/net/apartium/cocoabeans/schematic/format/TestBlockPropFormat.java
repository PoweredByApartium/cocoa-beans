package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;

class TestBlockPropFormat implements BlockPropFormat<Integer> {

    private int encodeCallCount = 0;
    private int decodeCallCount = 0;

    @Override
    public BlockProp<Integer> decode(byte[] value) {
        decodeCallCount++;
        if (value.length != 4)
            throw new IllegalArgumentException("TestBlockPropFormat: expected 4 bytes, got " + value.length);

        int v = ((value[0] & 0xFF) << 24)
                | ((value[1] & 0xFF) << 16)
                | ((value[2] & 0xFF) << 8)
                | (value[3] & 0xFF);
        return new IntBlockProp(v);
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        encodeCallCount++;
        if (!(prop.value() instanceof Integer value))
            throw new IllegalArgumentException("TestBlockPropFormat: expected Integer, got " + prop.value().getClass().getSimpleName());

        return new byte[] {
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF),
        };
    }

    int getEncodeCallCount() { return encodeCallCount; }
    int getDecodeCallCount() { return decodeCallCount; }
}
