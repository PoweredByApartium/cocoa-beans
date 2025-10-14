package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.45")
public interface BlockPropFormat<T> {

    ByteBlockPropFormat BYTE = new ByteBlockPropFormat();
    StringBlockPropFormat STRING = new StringBlockPropFormat();
    ArrayStringBlockPropFormat ARRAY_STRING = new ArrayStringBlockPropFormat();

    BlockProp<T> decode(byte[] value);
    byte[] encode(BlockProp<?> prop);

}
