package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.0.46")
public record IntPropFormat(Function<Integer, BlockProp<Integer>> constructor) implements BlockPropFormat<Integer> {

    /**
     * Creates a new {@code IntPropFormat} using the default {@link IntBlockProp} constructor.
     */
    public IntPropFormat() {
        this(IntBlockProp::new);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reads a single 4-byte big-endian integer from {@code value}.</p>
     *
     * @throws IllegalArgumentException if {@code value} is not exactly 4 bytes
     */
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

    /**
     * {@inheritDoc}
     *
     * <p>Writes a single 4-byte big-endian integer.</p>
     *
     * @throws IllegalArgumentException if the prop value is not an {@link Integer}
     */
    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof Integer value))
            throw new IllegalArgumentException("Prop value isn't type of int instead: " + prop.value().getClass().getSimpleName());

        return new byte[] {
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF),
        };
    }
}
