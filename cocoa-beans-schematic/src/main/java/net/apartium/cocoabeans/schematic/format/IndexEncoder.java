package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy interface for encoding and decoding the spatial index section of a schematic file.
 *
 * <p>A schematic file is split into two sections: a <em>block-data</em> section (managed by a
 * {@link BlockDataEncoder}) that stores the raw block state for each distinct {@link BlockData},
 * and an <em>index</em> section (managed by this interface) that records <em>where</em> each
 * block placement sits in world space and which block-data entry it references.</p>
 *
 * <p>Implementations are free to choose any binary layout for the index section. The only
 * contract is that a byte array produced by {@link #write} can be fully reconstructed by
 * {@link #read} given the same {@link AxisOrder} and a compatible {@link BlockDataEncoder}.</p>
 *
 * @see BlockChunkIndexEncoder
 * @see BlockDataEncoder
 */
@ApiStatus.AvailableSince("0.0.46")
public interface IndexEncoder {

    /**
     * Deserialises the index section and returns an iterator over the block placements it encodes.
     *
     * @param indexIn          seekable stream positioned at the start of the index section
     * @param axisOrder        axis-to-index mapping that was used when the index was written;
     *                         must match the value passed to {@link #write}
     * @param blockDataEncoder encoder used to deserialise individual {@link BlockData} values
     *                         from {@code blockIn}
     * @param blockIn          seekable stream containing the raw block-data section
     * @return an iterator over every {@link net.apartium.cocoabeans.schematic.block.BlockPlacement}
     *         encoded in the index
     * @throws IOException if an I/O error occurs while reading either stream
     */
    BlockIterator read(SeekableInputStream indexIn, AxisOrder axisOrder, BlockDataEncoder blockDataEncoder, SeekableInputStream blockIn) throws IOException;

    /**
     * Serialises a sequence of block placements into the binary index format.
     *
     * <p>Each placement is mapped to the corresponding entry in {@code blockIndexes} so that the
     * index stores byte offsets into the block-data section rather than inline block state.</p>
     *
     * @param placements   iterator over the block placements to encode
     * @param axisOrder    axis-to-index mapping to apply during encoding
     * @param blockIndexes map from each {@link BlockData} value to its byte offset in the
     *                     block-data stream; every {@link BlockData} referenced by
     *                     {@code placements} must have an entry
     * @return the serialised index as a byte array, ready to be embedded in a schematic file
     */
    byte[] write(BlockIterator placements, AxisOrder axisOrder, Map<BlockData, Long> blockIndexes);

}
