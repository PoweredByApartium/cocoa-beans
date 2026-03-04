package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.ApiStatus;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

import static net.apartium.cocoabeans.utils.BufferUtils.*;
import static net.apartium.cocoabeans.utils.BufferUtils.writeU64;
import static net.apartium.cocoabeans.space.Position.add;
import static net.apartium.cocoabeans.space.Position.multiply;

/**
 * An {@link IndexEncoder} that organises block placements in a recursive quadtree-like chunk
 * hierarchy and serialises that hierarchy to a compact binary index.
 *
 * <h2>Binary format</h2>
 * <pre>
 *   [1 byte]  layers   – tree depth; the spatial scale of the root is 4^layers
 *   [node…]  BFS-ordered chunk nodes (see below)
 * </pre>
 *
 * Each <em>chunk node</em> contains:
 * <pre>
 *   [8 bytes] mask     – 64-bit bitmask; bit {@code i} is set when child slot {@code i} is occupied
 *   [8 bytes] × popcount(mask)
 *             entries  – for leaf nodes: byte offset into the block-data stream;
 *                        for inner nodes: byte offset of the child chunk node
 * </pre>
 *
 * <p>Inner-node offsets are back-patched after the child node is written, so the format supports
 * forward references without a separate relocation table.</p>
 *
 * <h2>Spatial layout</h2>
 * <p>Each chunk covers a 4×4×4 region of block positions (see {@link BlockChunkImpl#SIZE}).
 * The three flat indices {@code i0}, {@code i1}, {@code i2} are mapped to world-space axes via
 * the supplied {@link AxisOrder}, allowing the encoder to be axis-agnostic.</p>
 *
 * @see IndexEncoder
 * @see BlockChunkImpl
 */
@ApiStatus.AvailableSince("0.0.46")
public class BlockChunkIndexEncoder implements IndexEncoder {

    /** Numeric identifier that distinguishes this encoder in the schematic file header. */
    public static final int ID = 0b1;

    /**
     * Deserialises a block-chunk index from {@code indexIn} and returns an iterator over the
     * block placements it describes.
     *
     * <p>The method reads the tree depth from the first byte, computes the root scaler as
     * {@code 4^layers}, and then recursively decodes every chunk node, resolving leaf offsets
     * against {@code blockIn} via {@code blockDataEncoder}.</p>
     *
     * @param indexIn          seekable stream positioned at the start of the index section
     * @param axisOrder        axis-to-index mapping used during spatial decoding
     * @param blockDataEncoder encoder used to deserialise individual {@link BlockData} values
     * @param blockIn          seekable stream containing the raw block data
     * @return an iterator over the decoded {@link BlockPlacement} values
     * @throws IOException if an I/O error occurs while reading either stream
     */
    @Override
    public BlockIterator read(SeekableInputStream indexIn, AxisOrder axisOrder, BlockDataEncoder blockDataEncoder, SeekableInputStream blockIn) throws IOException {
        int layers = indexIn.read();
        long scaler = (long) Math.pow(4, layers);

        MutableBlockChunk[] blockChunk = new MutableBlockChunk[]{new MutableBlockChunkImpl(axisOrder, 1, Position.ZERO, Position.ZERO)};

        readIndexLayer(
                indexIn,
                new DataInputStream(indexIn),
                blockChunk,
                indexIn.position(),
                scaler,
                Position.ZERO,
                axisOrder,
                blockDataEncoder,
                blockIn
        );

        return new BlockChunkIterator(blockChunk[0]);
    }

    /**
     * Recursively decodes one layer of the chunk-index tree.
     *
     * <p>The method seeks {@code in} to {@code fileOffset}, reads the 64-bit mask, and iterates
     * over every set bit. For each occupied slot it computes the world-space position by
     * combining {@code actualPosition} with the chunk-local offset scaled by {@code scaler}.</p>
     *
     * <ul>
     *   <li>When {@code scaler == 1} the slot is a leaf: the stored value is a byte offset into
     *       the block-data stream and a single {@link BlockPlacement} is added to
     *       {@code blockChunk}.</li>
     *   <li>Otherwise the stored value is an offset to a child chunk node, and the method calls
     *       itself recursively with {@code scaler / 4}.</li>
     * </ul>
     *
     * <p>After processing each slot the stream position is advanced by 8 bytes so that the next
     * sibling offset lines up correctly regardless of recursion depth.</p>
     *
     * @param in              seekable stream used for positioning
     * @param din             data-input wrapper around {@code in} for reading multi-byte values
     * @param blockChunk      single-element array holding the accumulator chunk (allows mutation
     *                        across recursive calls without extra state)
     * @param fileOffset      absolute position in {@code in} at which this node's data begins
     * @param scaler          spatial scale of this tree level; {@code 1} means leaf level
     * @param actualPosition  world-space origin of the current chunk
     * @param axes            axis-to-index mapping
     * @param encoder         used to read a {@link BlockData} value from {@code blockIn}
     * @param blockIn         seekable stream containing raw block data
     * @throws IllegalStateException if {@code scaler} is negative
     * @throws EOFException          if a leaf entry is missing from the index stream
     * @throws IOException           if an I/O error occurs
     */
    private void readIndexLayer(SeekableInputStream in, DataInputStream din, MutableBlockChunk[] blockChunk, long fileOffset, long scaler, Position actualPosition, AxisOrder axes, BlockDataEncoder encoder, SeekableInputStream blockIn) throws IOException {
        if (scaler < 0)
            throw new IllegalStateException("Scaler must be positive!");

        in.position(fileOffset);

        boolean[] mask = toBooleanArray(readU64(din));
        for (int i = 0; i < mask.length; i++) {
            if (!mask[i])
                continue;

            int i0 = i % BlockChunkImpl.SIZE;
            int i1 = (i / BlockChunkImpl.SIZE) % BlockChunkImpl.SIZE;
            int i2 = i / (BlockChunkImpl.SIZE * BlockChunkImpl.SIZE);

            Position chunkPoint = axes.position(i0, i1, i2);
            Position actualPos = add(actualPosition, multiply(chunkPoint, scaler));

            long offsetBefore = in.position();
            if (scaler == 1) {
                // READ Block
                if (in.position() >= in.size())
                    throw new EOFException("Something went wrong");

                long newPos = readU64(din);
                blockIn.position(newPos);

                blockChunk[0] = setBlock(blockChunk[0], new BlockPlacement(actualPos, encoder.read(blockIn)));
                in.position(offsetBefore + 8);
                continue;
            }

            long nextFileOffset = readU64(din);
            readIndexLayer(
                    in,
                    din,
                    blockChunk,
                    nextFileOffset,
                    scaler / 4,
                    actualPos,
                    axes,
                    encoder,
                    blockIn
            );

            in.position(offsetBefore + 8L);
        }
    }

    /**
     * Serialises the given block placements into the binary chunk-index format.
     *
     * <p>All placements are first inserted into a {@link MutableBlockChunk} tree, which is then
     * serialised layer by layer using a BFS traversal. The returned byte array can be embedded
     * directly in a schematic file as the index section.</p>
     *
     * @param placements   iterator over the block placements to encode
     * @param axisOrder    axis-to-index mapping used during tree construction
     * @param blockIndexes map from each {@link BlockData} to its byte offset in the block-data
     *                     stream; every block referenced by {@code placements} must have an entry
     * @return the serialised index as a byte array
     * @throws UncheckedIOException if an I/O error occurs during serialisation
     */
    @Override
    public byte[] write(BlockIterator placements, AxisOrder axisOrder, Map<BlockData, Long> blockIndexes) {
        ByteArraySeekableChannel channel = new ByteArraySeekableChannel();
        SeekableOutputStream indexesOut = new SeekableOutputStream(channel);

        MutableBlockChunk blockChunk = convertToBlockChunk(placements, axisOrder);
        try {
            writeIndexes(indexesOut, (long) blockChunk.getScaler(), blockChunk, blockIndexes);
            return channel.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Converts a flat sequence of block placements into a {@link MutableBlockChunk} tree.
     *
     * <p>Placements are inserted one by one; the root chunk is automatically expanded whenever a
     * placement falls outside the current spatial bounds.</p>
     *
     * @param placements iterator over the placements to insert
     * @param axisOrder  axis-to-index mapping for the resulting chunk tree
     * @return the root of the populated chunk tree
     */
    private MutableBlockChunk convertToBlockChunk(Iterator<BlockPlacement> placements, AxisOrder axisOrder) {
        MutableBlockChunk blockChunk = new MutableBlockChunkImpl(axisOrder, 1, Position.ZERO, Position.ZERO);
        while (placements.hasNext())
            blockChunk = setBlock(blockChunk, placements.next());

        return blockChunk;
    }

    /**
     * Inserts a single block placement into the chunk tree, growing the root if necessary.
     *
     * <p>If the placement's position exceeds the current root's scale, a new, larger root is
     * created that wraps the existing tree, and the process repeats until the position fits.</p>
     *
     * @param blockChunk the current root chunk
     * @param placement  the placement to insert
     * @return the (possibly replaced) root chunk after insertion
     */
    private MutableBlockChunk setBlock(MutableBlockChunk blockChunk, BlockPlacement placement) {
        Position pos = placement.position();

        int maxAxis = (int) Math.max(pos.getX(), Math.max(pos.getY(), pos.getZ()));
        if (maxAxis >= blockChunk.getScaler())
            blockChunk = new MutableBlockChunkImpl(blockChunk.getAxisOrder(), Mathf.nextPowerOfFour(maxAxis) * 4.0, Position.ZERO, Position.ZERO, blockChunk);

        blockChunk.setBlock(placement);
        return blockChunk;
    }

    /**
     * Serialises the entire chunk tree to {@code indexesOut} using a BFS traversal.
     *
     * <p>The method writes the layer count, then processes chunk nodes level by level. Inner-node
     * child offsets are initially written as zero and back-patched by
     * {@link #pointParentToChild} once the child's position in the output stream is known.</p>
     *
     * <p>If the root chunk's mask is zero (no blocks), only the layer count and an empty mask are
     * written and the method returns early.</p>
     *
     * @param indexesOut   output stream to write the index into
     * @param scaler       spatial scale of the root chunk (must equal {@code 4^layers})
     * @param blockChunk   root of the chunk tree to serialise
     * @param blockIndexes map from {@link BlockData} to byte offset in the block-data stream
     * @throws IOException if an I/O error occurs
     */
    private void writeIndexes(SeekableOutputStream indexesOut, long scaler, MutableBlockChunk blockChunk, Map<BlockData, Long> blockIndexes) throws IOException {
        Map<Pointer, Long> pointerFileOffsets = new IdentityHashMap<>();
        Map<Pointer, Entry<Pointer, Integer>> pointToParent = new IdentityHashMap<>();

        int layers = (int) Math.ceil(Mathf.log4(scaler));
        indexesOut.write((byte) layers);

        ChunkPointer rootPointer = new ChunkPointer(blockChunk);
        if (rootPointer.getChunk().getMask() == 0L) {
            indexesOut.write(writeU64(0L));
            return;
        }

        pointToParent.put(rootPointer, new Entry<>(rootPointer, 0));

        List<Pointer> nextInQueue = new ArrayList<>();
        nextInQueue.add(rootPointer);

        while (!nextInQueue.isEmpty()) {
            List<Pointer> currentPointers = new ArrayList<>(nextInQueue);
            nextInQueue.clear();

            while (!currentPointers.isEmpty()) {
                Pointer pointer = currentPointers.remove(0);
                long position = indexesOut.position();
                pointerFileOffsets.put(pointer, position);

                if (pointer instanceof ChunkPointer chunkPointer) {
                    writeChunkPointer(
                            indexesOut,
                            chunkPointer,
                            pointerFileOffsets,
                            pointToParent,
                            nextInQueue,
                            blockIndexes,
                            pointer != rootPointer
                    );
                    continue;
                }

                if (pointer instanceof BlockPointer)
                    throw new IllegalStateException("BlockPointers are not supported while outside of chunk pointers");



                throw new UnsupportedOperationException("Unsupported pointer type: " + pointer.getClass().getName());
            }
        }
    }

    /**
     * Writes a single chunk-pointer node to {@code out}.
     *
     * <p>If {@code shouldPoint} is {@code true}, the parent's placeholder offset for this node is
     * back-patched before the node data is written. The node itself consists of the 64-bit mask
     * followed by one 8-byte entry per set bit:</p>
     * <ul>
     *   <li>{@link BlockPointer} children are written as their block-data stream offset.</li>
     *   <li>{@link ChunkPointer} children are enqueued in {@code nextInQueue} and written as a
     *       zero placeholder that will be back-patched later.</li>
     * </ul>
     *
     * @param out               output stream
     * @param pointer           the chunk pointer to write
     * @param pointerFileOffsets map tracking the stream offset at which each pointer was written
     * @param pointToParent     map from each pointer to its parent pointer and slot index
     * @param nextInQueue       accumulator for child chunk pointers to process in the next BFS wave
     * @param blockIndexes      map from {@link BlockData} to byte offset in the block-data stream
     * @param shouldPoint       {@code true} if the parent placeholder should be back-patched
     * @throws IllegalStateException if the chunk mask is zero or a duplicate pointer is detected
     * @throws IOException           if an I/O error occurs
     */
    private void writeChunkPointer(SeekableOutputStream out, ChunkPointer pointer, Map<Pointer, Long> pointerFileOffsets, Map<Pointer, Entry<Pointer, Integer>> pointToParent, List<Pointer> nextInQueue, Map<BlockData, Long> blockIndexes, boolean shouldPoint) throws IOException {
        Entry<Pointer, Integer> entry = pointToParent.get(pointer);
        if (shouldPoint)
            pointParentToChild(out, out.position(), pointerFileOffsets.get(entry.key()), entry.value());

        BlockChunk blockChunk = pointer.getChunk();
        long mask = blockChunk.getMask();

        if (mask == 0L)
            throw new IllegalStateException("Chunk mask is zero");

        out.write(writeU64(mask));
        List<Pointer> pointers = blockChunk.getPointers();


        for (int i = 0; i < pointers.size(); i++) {
            Pointer child = pointers.get(i);
            if (pointToParent.containsKey(child))
                throw new IllegalStateException("Duplicate pointer at " + child);

            if (child instanceof BlockPointer blockPointer) {
                Long fileOffset = blockIndexes.get(blockPointer.getData());
                if (fileOffset == null)
                    throw new IllegalStateException("Couldn't find block index for block " + blockPointer.getData());

                out.write(writeU64(fileOffset));
                continue;
            }

            pointToParent.put(child, new Entry<>(pointer, i));
            nextInQueue.add(child);
            out.write(new byte[8]);
        }

    }

    /**
     * Back-patches the placeholder offset written by the parent node so that it points to the
     * child node that has just been written.
     *
     * <p>The method seeks to {@code parentOffset + 8 + index * 8} (skipping the parent's 8-byte
     * mask and {@code index} prior entries), writes the child's absolute stream offset, then
     * restores the stream position to {@code offset} so subsequent writes continue correctly.</p>
     *
     * @param indexesOut   the output stream to patch
     * @param offset       absolute stream position of the child node
     * @param parentOffset absolute stream position at which the parent node was written
     * @param index        slot index within the parent's entry list that should point to the child
     * @throws IOException if an I/O error occurs
     */
    private void pointParentToChild(SeekableOutputStream indexesOut, long offset, long parentOffset, int index) throws IOException {
        indexesOut.position(parentOffset + 8 + index * 8L);
        indexesOut.write(writeU64(offset));
        indexesOut.position(offset);
    }

}
