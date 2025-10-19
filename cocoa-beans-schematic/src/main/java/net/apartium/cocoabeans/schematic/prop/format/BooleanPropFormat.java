package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;

import java.util.function.Function;

public record BooleanPropFormat(Function<Boolean, BlockProp<Boolean>> constructor) implements BlockPropFormat<Boolean> {

    public BooleanPropFormat() {
        this(BooleanBlockProp::new);
    }

    @Override
    public BlockProp<Boolean> decode(byte[] value) {
        if (value.length != 1)
            throw new IllegalArgumentException("Expected array of size 1 and got " + value.length);

        return constructor.apply(value[0] == 1);
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof Boolean value))
            return null;

        return new byte[]{(byte) (value ? 1 : 0)};
    }
}
