package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface BlockDataEncoder {

    BlockData read(SeekableInputStream stream);
    byte[] write(BlockData blockData);

}
