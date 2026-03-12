package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

/**
 * An encoder/decoder responsible for converting block props to and from binary form
 * @param <T>
 * @see BlockProp
 */
@ApiStatus.AvailableSince("0.0.46")
public interface BlockPropFormat<T> {

    /**
     * Decodes a binary representation into a {@link BlockProp}.
     *
     * @param value the raw bytes to decode
     * @return the decoded block prop
     */
    BlockProp<T> decode(byte[] value);

    /**
     * Encodes a {@link BlockProp} into its binary representation.
     *
     * @param prop the block prop to encode
     * @return the encoded bytes
     */
    byte[] encode(BlockProp<?> prop);

}
