package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.seekable.SeekableInputStream;

import java.io.IOException;
import java.util.Map;

public interface IndexEncoder {

    BlockIterator read(SeekableInputStream indexIn, AxisOrder axisOrder, BlockDataEncoder blockDataEncoder, SeekableInputStream blockIn) throws IOException;
    byte[] write(BlockIterator placements, AxisOrder axisOrder, Map<BlockData, Long> blockIndexes);

}
