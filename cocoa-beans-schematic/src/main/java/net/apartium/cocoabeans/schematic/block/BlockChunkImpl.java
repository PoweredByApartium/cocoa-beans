package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

/**
 * Implementation of the {@link BlockChunk} interface representing a batch of blocks in a schematic.
 * Handles block pointers, chunk positions, and mask management for efficient block access.
 * @see BlockData
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public class BlockChunkImpl implements BlockChunk {

    /**
     * Size of the chunk along each axis.
     */
    public static final int SIZE = 4;

    /* package-private */ final AxisOrder axisOrder;
    /* package-private */ final double scaler;
    /* package-private */ final double nextScaler;
    /* package-private */ final Position actualPos;
    /* package-private */ final Position chunkPos;
    /**
     * Array of pointers to block or chunk data. May be empty.
     */
    /* package-private */ Pointer[] pointers = new Pointer[0];
    /**
     * Bitmask representing which blocks are present in the chunk.
     */
    /* package-private */ long mask = 0;

    /**
     * Constructs a new immutable BlockChunkImpl from an existing BlockChunk.
     * Copies pointers and mask from the source chunk.
     * @param chunk the source BlockChunk to copy
     */
    public BlockChunkImpl(BlockChunk chunk) {
        this.axisOrder = chunk.getAxisOrder();
        this.scaler = chunk.getScaler();
        this.nextScaler = Math.floor(scaler / SIZE);
        this.actualPos = chunk.getActualPos();
        this.chunkPos = chunk.getChunkPos();

        // Copy pointers as immutable
        this.mask = chunk.getMask();

        List<Pointer> chunkPointers = chunk.getPointers();
        this.pointers = new Pointer[chunkPointers.size()];

        for (int i = 0; i < chunkPointers.size(); i++) {
            Pointer pointer = chunkPointers.get(i);
            if (pointer instanceof ChunkPointer chunkPointer) {
                pointer = new ChunkPointer(
                        create(chunkPointer.getChunk())
                );
            } else if (pointer instanceof BlockPointer blockPointer) {
                pointer = new BlockPointer(
                        blockPointer.getData()
                );
            } else {
                throw new IllegalArgumentException("Invalid pointer type: " + pointer.getClass().getSimpleName());
            }

            this.pointers[i] = pointer;
        }
    }

    /**
     * Constructs a BlockChunkImpl with specified parameters and optionally a previous chunk.
     * Used internally for chunk creation and pointer management.
     * @param axisOrder the axis order for block arrangement
     * @param scaler the scaling factor for chunk size
     * @param actualPos the actual position of the chunk
     * @param chunkPos the chunk position
     * @param prev the previous chunk, may be null
     */
    protected BlockChunkImpl(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, @Nullable BlockChunk prev) {
        this.axisOrder = axisOrder;
        this.scaler = scaler;
        this.nextScaler = Math.floor(scaler / SIZE);
        this.actualPos = actualPos;
        this.chunkPos = chunkPos;
        if (prev != null && scaler != 0) {
            mask = 1;
            if (prev.getScaler() != nextScaler) {
                pointers = new Pointer[]{new ChunkPointer(create(axisOrder, nextScaler, actualPos, chunkPos, prev))};
            } else {
                pointers = new Pointer[]{new ChunkPointer(prev)};
            }
        }
    }

    /**
     * Constructs a BlockChunkImpl with specified parameters and no previous chunk.
     * @param axisOrder the axis order for block arrangement
     * @param scaler the scaling factor for chunk size
     * @param actualPos the actual position of the chunk
     * @param chunkPos the chunk position
     */
    public BlockChunkImpl(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos) {
        this(axisOrder, scaler, actualPos, chunkPos, null);
    }

    /**
     * Creates a new BlockChunkImpl from an existing BlockChunk.
     * @param blockChunk the source BlockChunk
     * @return a new BlockChunkImpl instance
     */
    protected BlockChunk create(BlockChunk blockChunk) {
        return new BlockChunkImpl(blockChunk);
    }

    /**
     * Creates a new BlockChunkImpl with specified parameters and previous chunk.
     * @param axisOrder the axis order for block arrangement
     * @param scaler the scaling factor for chunk size
     * @param actualPos the actual position of the chunk
     * @param chunkPos the chunk position
     * @param prev the previous chunk
     * @return a new BlockChunkImpl instance
     */
    protected BlockChunk create(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, BlockChunk prev) {
        return new BlockChunkImpl(
                axisOrder,
                scaler,
                actualPos,
                chunkPos,
                prev
        );
    }

    protected Position getChunkPos(Position pos) {
        Position chunkPos = new Position(pos).subtract(actualPos).divide(scaler).floor();
        if (chunkPos.getX() >= SIZE ||  chunkPos.getY() >= SIZE || chunkPos.getZ() >= SIZE)
            throw new IllegalStateException("TF: (" + chunkPos + ") (" + pos + ")");

        return chunkPos;
    }

    protected OptionalInt getIndex(Position pos) {
        if (axisOrder.compare(pos, actualPos) < 0)
            return OptionalInt.empty();

        Position chunkPos = getChunkPos(pos);

        int i0 = (int) axisOrder.getFirst().getAlong(chunkPos);
        int i1 = (int) axisOrder.getSecond().getAlong(chunkPos);
        int i2 = (int) axisOrder.getThird().getAlong(chunkPos);

        return OptionalInt.of(i0 + (i1 * SIZE) + (i2 * SIZE * SIZE));
    }

    protected OptionalInt getCountBits(Integer index) {
        if (((mask >> index) & 1) == 0)
            return OptionalInt.empty();

        return OptionalInt.of(countBits(mask, index) - 1);
    }

    /**
     * Retrieves the block data at the specified position.
     * Returns null if the position is outside the chunk or not present in the mask.
     * @param pos the position to query
     * @return the BlockData at the position, or null if not present
     */
    @Nullable
    @Override
    public BlockData getBlock(Position pos) {
        OptionalInt optIndex = getIndex(pos);
        if (optIndex.isEmpty())
            return null;

        OptionalInt optCount = getCountBits(optIndex.getAsInt());
        if (optCount.isEmpty())
            return null;

        int count = optCount.getAsInt();

        Pointer pointer = pointers[count];
        if (pointer instanceof BlockPointer blockPointer)
            return blockPointer.getData();

        if (pointer instanceof ChunkPointer chunkPointer)
            return chunkPointer.getChunk().getBlock(pos);

        Ensures.notNull(pointer, "Something went wrong with pointers");
        throw new UnsupportedOperationException("Not supported yet: " + pointer.getClass().getName());
    }


    /**
     * Counts the number of set bits in the mask up to the specified index.
     * Used for pointer indexing.
     * @param mask the bitmask
     * @param index the index up to which to count
     * @return the number of set bits
     */
    static int countBits(long mask, int index) {
        int count = 0;
        for (int i = index; i >= 0; i--) {
            if ((mask & (1L << i)) != 0) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns a copy of the pointers array.
     * @return a list of pointers
     */
    @Override
    public List<Pointer> getPointers() {
        return Arrays.asList(this.pointers);
    }

    /**
     * Returns the bitmask representing block presence in the chunk.
     * @return the mask value
     */
    @Override
    public long getMask() {
        return mask;
    }

    /**
     * Returns the scaling factor for the chunk.
     * @return the scaler value
     */
    @Override
    public double getScaler() {
        return scaler;
    }

    /**
     * Returns the actual position of the chunk.
     * @return the actual position
     */
    @Override
    public Position getActualPos() {
        return actualPos;
    }

    /**
     * Returns the chunk position.
     * @return the chunk position
     */
    @Override
    public Position getChunkPos() {
        return chunkPos;
    }

    /**
     * Returns the axis order used for block arrangement.
     * @return the axis order
     */
    @Override
    public AxisOrder getAxisOrder() {
        return axisOrder;
    }

    /**
     * Returns the size of the entire chunk as an AreaSize object.
     * If the mask is empty, returns zero size.
     * @return the size of the chunk
     */
    @Override
    public AreaSize getSizeOfEntireChunk() {
        if (mask == 0)
            return new AreaSize(0, 0, 0);

        if (scaler == 1) {
            int bitPos = Long.numberOfTrailingZeros(mask);

            int i0 = bitPos % SIZE;
            int i1 = (bitPos / SIZE) % SIZE;
            int i2 = bitPos / (SIZE * SIZE);

            Position pos = axisOrder.position(i0, i1, i2).add(actualPos);

            return new AreaSize(
                    pos.getX() + 1,
                    pos.getY() + 1,
                    pos.getZ() + 1
            );
        }

        return ((ChunkPointer) pointers[pointers.length - 1])
                .getChunk()
                .getSizeOfEntireChunk();
    }

    /**
     * Returns a mutable version of this chunk.
     * @return a MutableBlockChunk instance
     */
    @Override
    public MutableBlockChunk mutable() {
        return new MutableBlockChunkImpl(this);
    }

    /**
     * Returns an immutable version of this chunk (itself).
     * @return this BlockChunkImpl instance
     */
    @Override
    public BlockChunk immutable() {
        return this;
    }
}
