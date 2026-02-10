package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents a chunk of blocks in a schematic, providing access to block data, chunk position, and chunk properties.
 * Implementations may be mutable or immutable.
 */
@NullMarked
public interface BlockChunk {

    /**
     * Creates an empty mutable block chunk with default parameters.
     *
     * @return a new empty {@link MutableBlockChunk}
     */
    static MutableBlockChunk empty() {
        return new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
    }

    /**
     * Returns the pointers for this chunk. The pointers represent block locations or references.
     * @return an array of {@link Pointer} objects
     */
    // todo make not array
    Pointer[] getPointers();

    /**
     * Gets the block data at the specified position.
     * @param pos the position to query
     * @return the {@link BlockData} at the position, or {@code null} if none exists
     */
    @Nullable
    BlockData getBlock(Position pos);

    /**
     * Returns the mask value for this chunk, which may represent block presence or metadata.
     * @return the mask as a long value
     */
    long getMask();

    /**
     * Returns the scaler value for this chunk, which may be used for scaling block coordinates.
     * @return the scaler as a double value
     */
    double getScaler();

    /**
     * Returns the actual position of this chunk in the schematic space.
     * @return the actual {@link Position}
     */
    Position getActualPos();

    /**
     * Returns the chunk position (grid-aligned position) of this chunk.
     * @return the chunk {@link Position}
     */
    Position getChunkPos();

    /**
     * Returns the axis order used for this chunk (e.g., XYZ, XZY).
     * @return the {@link AxisOrder}
     */
    AxisOrder getAxisOrder();

    /**
     * Returns the size of the entire chunk.
     * @return the {@link AreaSize} representing the chunk size
     */
    AreaSize getSizeOfEntireChunk();

    /**
     * Returns a mutable version of this chunk.
     * @return a {@link MutableBlockChunk}
     */
    MutableBlockChunk mutable();

    /**
     * Returns an immutable version of this chunk.
     * @return a {@link BlockChunk} that is immutable
     */
    BlockChunk immutable();

}
