package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.Map;

@ApiStatus.AvailableSince("0.0.46")
public interface IndexEncoder {

    BlockIterator read(SeekableInputStream indexIn, AxisOrder axisOrder, BlockDataEncoder blockDataEncoder, SeekableInputStream blockIn) throws IOException;
    byte[] write(BlockIterator placements, AxisOrder axisOrder, Map<BlockData, Long> blockIndexes);

}
