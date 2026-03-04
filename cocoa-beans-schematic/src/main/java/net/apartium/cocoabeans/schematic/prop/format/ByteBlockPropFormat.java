package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.ByteBlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.0.46")
public record ByteBlockPropFormat(Function<Byte, BlockProp<Byte>> constructor) implements BlockPropFormat<Byte> {

    /**
     * Creates a new {@code ByteBlockPropFormat} using the default {@link ByteBlockProp} constructor.
     */
    public ByteBlockPropFormat() {
        this(ByteBlockProp::new);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if {@code value} is not exactly 1 byte
     */
    @Override
    public BlockProp<Byte> decode(byte[] value) {
        if (value.length != 1)
            throw new IllegalArgumentException("Expected array of size 1 and got " + value.length);

        return constructor.apply(value[0]);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the prop value is not a {@link Byte}
     */
    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof Byte value))
            throw new IllegalArgumentException("Prop value isn't type of byte instead: " + prop.value().getClass().getSimpleName());

        return new byte[]{value};
    }
}
