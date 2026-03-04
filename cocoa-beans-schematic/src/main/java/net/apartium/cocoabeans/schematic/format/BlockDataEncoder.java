package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import org.jetbrains.annotations.ApiStatus;

/**
 * Strategy interface for encoding and decoding individual {@link BlockData} values in a schematic
 * file's block-data section.
 *
 * <p>The block-data section stores one serialised entry per distinct {@link BlockData} value. An
 * {@link IndexEncoder} references these entries by their byte offset, so each implementation must
 * produce a self-contained, fixed-boundary record that can be seeked to and read independently.</p>
 *
 * <p>A byte array produced by {@link #write} must be fully reconstructable by {@link #read} when
 * the stream is positioned at the start of that array.</p>
 *
 * @see SimpleBlockDataEncoder
 * @see IndexEncoder
 */
@ApiStatus.AvailableSince("0.0.46")
public interface BlockDataEncoder {

    /**
     * Deserialises a single {@link BlockData} value from the current position of {@code stream}.
     *
     * <p>The stream must be positioned at the beginning of a block-data entry. After this method
     * returns, the stream position should be immediately past the entry that was read.</p>
     *
     * @param stream the seekable stream to read from
     * @return the decoded {@link BlockData}
     */
    BlockData read(SeekableInputStream stream);

    /**
     * Serialises a {@link BlockData} value into a self-contained byte array.
     *
     * <p>The returned array must be a complete, stand-alone record: passing it to {@link #read}
     * (via a stream wrapping the array) must reproduce an equivalent {@link BlockData}.</p>
     *
     * @param blockData the block data to serialise
     * @return the serialised bytes
     */
    byte[] write(BlockData blockData);

}
