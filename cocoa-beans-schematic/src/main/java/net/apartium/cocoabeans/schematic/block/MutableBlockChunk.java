package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.Position;
import org.jspecify.annotations.NullMarked;

/**
 * A mutable chunk of blocks, allowing modification of block data.
 * Extends {@link BlockChunk} to provide methods for setting and removing blocks.
 */
@NullMarked
public interface MutableBlockChunk extends BlockChunk {

    /**
     * Sets a block in the chunk at the specified placement.
     *
     * @param placement the block placement information
     * @return true if the block was set successfully, false otherwise
     */
    boolean setBlock(BlockPlacement placement);

    /**
     * Removes the block at the given position.
     *
     * @param position the position of the block to remove
     * @return the removed {@link BlockData}, or null if no block was present
     */
    BlockData removeBlock(Position position);

}
