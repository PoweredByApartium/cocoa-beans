package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

/**
 * An encoder/decoder class responsible for converting block props to and from binary from
 * @param <T>
 * @see BlockProp
 */
@ApiStatus.AvailableSince("0.0.46")
public interface BlockPropFormat<T> {

    BlockProp<T> decode(byte[] value);
    byte[] encode(BlockProp<?> prop);

}
