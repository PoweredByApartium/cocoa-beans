package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface BlockPropFormat<T> {

    ByteBlockPropFormat BYTE = new ByteBlockPropFormat();
    StringBlockPropFormat STRING = new StringBlockPropFormat();
    StringArrayBlockPropFormat ARRAY_STRING = new StringArrayBlockPropFormat();

    BlockProp<T> decode(byte[] value);
    byte[] encode(BlockProp<?> prop);

}
