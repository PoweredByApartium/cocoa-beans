package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.IntBlockProp;

import java.util.function.Function;

public record IntPropFormat(Function<Integer, BlockProp<Integer>> constructor) implements BlockPropFormat<Integer> {

    public IntPropFormat() {
        this(IntBlockProp::new);
    }

    @Override
    public BlockProp<Integer> decode(byte[] value) {
        if (value.length != 4)
            throw new IllegalArgumentException("Expected array of size 4 and got " + value.length);

        return constructor.apply(
                (value[0] & 0xFF) << 24
                        | (value[1] & 0xFF) << 16
                        | (value[2] & 0xFF) << 8
                        | (value[3] & 0xFF)
        );
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof Integer value))
            return null;

        return new byte[] {
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF),
        };
    }
}
