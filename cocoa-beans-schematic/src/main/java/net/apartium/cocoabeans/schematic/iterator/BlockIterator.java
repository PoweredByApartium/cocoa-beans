package net.apartium.cocoabeans.schematic.iterator;

import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Iterator;

/**
 * An iterator for a 3d block-based map
 * @see BlockChunkIterator
 * @see SortedAxisBlockIterator
 */
@ApiStatus.AvailableSince("0.0.46")
public interface BlockIterator extends Iterator<BlockPlacement> {

    /**
     * Returns the current block position of the iterator
     * @return current block position of the iterator, or null if the iteration has no next value
     */
    @Nullable Position current();

    default @NonNull BlockChunk toBlockChunk(AxisOrder axisOrder) {
        MutableBlockChunkImpl chunk = (MutableBlockChunkImpl) BlockChunk.empty();
        while (hasNext()) {
            BlockPlacement placement = next();

            // todo kfir what the FUCK
            HomoUtils.rescaleChunkIfNeeded(chunk, axisOrder, placement.position());
            chunk.setBlock(placement);
        }

        return chunk.immutable();
    }
}
