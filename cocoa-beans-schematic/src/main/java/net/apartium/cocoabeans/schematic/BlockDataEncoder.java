package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.45")
public interface BlockDataEncoder {

    BlockData read(SeekableInputStream stream);
    byte[] write(BlockData blockData);

}
