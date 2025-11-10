package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.ByteBlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.0.46")
public record ByteBlockPropFormat(Function<Byte, BlockProp<Byte>> constructor) implements BlockPropFormat<Byte> {

    public ByteBlockPropFormat() {
        this(ByteBlockProp::new);
    }

    @Override
    public BlockProp<Byte> decode(byte[] value) {
        if (value.length != 1)
            throw new IllegalArgumentException("Expected array of size 1 and got " + value.length);

        return constructor.apply(value[0]);
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof Byte value))
            return null;

        return new byte[]{value};
    }
}
