package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.ByteBlockProp;

public class ByteBlockPropFormat implements BlockPropFormat<Byte> {

    @Override
    public ByteBlockProp decode(byte[] value) {
        if (value.length != 1)
            throw new IllegalArgumentException("Expected array of size 1 and got " + value.length);

        return ByteBlockProp.BYTE.apply(value[0]);
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof Byte value))
            return null;

        return new byte[]{value};
    }
}
