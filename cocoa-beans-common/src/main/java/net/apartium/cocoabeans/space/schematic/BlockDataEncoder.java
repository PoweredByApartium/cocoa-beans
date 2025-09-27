package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.schematic.utils.SeekableInputStream;

public interface BlockDataEncoder {

    BlockData read(SeekableInputStream stream);
    byte[] write(BlockData blockData);

}
