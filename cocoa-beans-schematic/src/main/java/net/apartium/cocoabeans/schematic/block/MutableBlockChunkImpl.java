package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.OptionalInt;

/**
 * MutableBlockChunkImpl provides a mutable implementation of BlockChunk,
 * allowing for dynamic modification of block data within a chunk.
 * Extends BlockChunkImpl and implements MutableBlockChunk.
 * Supports block placement, removal, and chunk mutation operations.
 */
@NullMarked
public class MutableBlockChunkImpl extends BlockChunkImpl implements MutableBlockChunk {

    /**
     * Constructs a mutable block chunk with a previous chunk reference.
     * @param axisOrder The axis order for block arrangement.
     * @param scaler The scaling factor for chunk size.
     * @param actualPos The actual position of the chunk.
     * @param chunkPos The logical position of the chunk.
     * @param prev The previous block chunk.
     */
    public MutableBlockChunkImpl(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, @Nullable  BlockChunk prev) {
        super(axisOrder, scaler, actualPos, chunkPos, prev);
    }

    /**
     * Constructs a mutable block chunk without a previous chunk reference.
     * @param axisOrder The axis order for block arrangement.
     * @param scaler The scaling factor for chunk size.
     * @param actualPos The actual position of the chunk.
     * @param chunkPos The logical position of the chunk.
     */
    public MutableBlockChunkImpl(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos) {
        super(axisOrder, scaler, actualPos, chunkPos);
    }

    /**
     * Constructs a mutable block chunk from an existing chunk.
     * @param chunk The source block chunk.
     */
    public MutableBlockChunkImpl(BlockChunk chunk) {
        super(chunk);
    }

    /**
     * Creates a new MutableBlockChunkImpl from an existing block chunk.
     * @param blockChunk The source block chunk.
     * @return A new MutableBlockChunkImpl instance.
     */
    @Override
    protected BlockChunk create(BlockChunk blockChunk) {
        return new MutableBlockChunkImpl(blockChunk);
    }

    /**
     * Creates a new MutableBlockChunkImpl with specified parameters.
     * @param axisOrder The axis order.
     * @param scaler The scaling factor.
     * @param actualPos The actual position.
     * @param chunkPos The chunk position.
     * @param prev The previous chunk.
     * @return A new MutableBlockChunkImpl instance.
     */
    @Override
    protected BlockChunk create(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, @Nullable BlockChunk prev) {
        return new MutableBlockChunkImpl(axisOrder, scaler, actualPos, chunkPos, prev);
    }

    /**
     * Updates the mask and pointer array for a given block index.
     * Ensures the mask reflects the presence of a block and shifts pointers accordingly.
     * @param index The block index to update.
     */
    private void updateMaskAndShift(int index) {
        if (((mask >> index) & 1) != 0)
            return;

        mask |= (1L << index);
        if (((mask >> index) & 1) == 0)
            throw new IllegalStateException("Something went wrong:\nmask: " + mask + "\nindex: " + index);

        int count = countBits(mask, index - 1);
        if (count == 0) {
            if (pointers.length == 0) {
                pointers = new Pointer[1];
                return;
            }

            Pointer[] clone = pointers;
            pointers = new Pointer[pointers.length + 1];
            System.arraycopy(clone, 0, pointers, 1, clone.length);
            return;
        }

        if (pointers.length == count) {
            Pointer[] clone = pointers;
            pointers = new Pointer[pointers.length + 1];
            System.arraycopy(clone, 0, pointers, 0, clone.length);
            return;
        }

        Pointer[] clone = pointers;
        pointers = new Pointer[pointers.length + 1];

        if (count >= 0)
            System.arraycopy(clone, 0, pointers, 0, count);

        if (pointers.length - (count + 1) >= 0)
            System.arraycopy(clone, count + 1 - 1, pointers, count + 1, pointers.length - (count + 1));
    }

    /**
     * Sets a pointer for a block placement at the specified indices and count.
     * Handles both block and chunk pointers.
     * @param placement The block placement information.
     * @param i0 First axis index.
     * @param i1 Second axis index.
     * @param i2 Third axis index.
     * @param count The pointer array index.
     * @return True if the pointer was set successfully, false otherwise.
     */
    private boolean setPointer(BlockPlacement placement, int i0, int i1, int i2, int count) {
        Pointer pointer = pointers[count];
        BlockData data = placement.block();

        if (pointer == null) {
            if (scaler == 1) {
                pointer = new BlockPointer(data);
            } else {
                Position chunkPoint = axisOrder.position(i0, i1, i2);
                pointer = new ChunkPointer(create(
                        axisOrder,
                        nextScaler,
                        new Position(actualPos).add(new Position(chunkPoint).multiply(scaler)),
                        chunkPoint,
                        null
                ));
                ((MutableBlockChunk) ((ChunkPointer) pointer).getChunk()).setBlock(placement);
            }

            pointers[count] = pointer;
            return true;

        }

        if (pointer instanceof BlockPointer blockPointer) {
            if (blockPointer.getData().equals(data))
                return true;

            pointers[count] = new BlockPointer(data);
            return true;
        }

        if (pointer instanceof ChunkPointer chunkPointer) {
            BlockChunk chunk = chunkPointer.getChunk();
            if (!(chunk instanceof MutableBlockChunk mutableChunk))
                throw new IllegalArgumentException("Unexpected chunk type: " + chunk.getClass().getSimpleName());

            return mutableChunk.setBlock(placement);
        }

        return false;
    }

    /**
     * Sets a block at the specified placement position.
     * @param placement The block placement information.
     * @return True if the block was set successfully, false otherwise.
     */
    @Override
    public boolean setBlock(BlockPlacement placement) {
        Position pos = placement.position();

        if (axisOrder.compare(pos, actualPos) < 0)
            return false;

        Position chunkPos = getChunkPos(pos);

        int i0 = (int) axisOrder.getFirst().getAlong(chunkPos);
        int i1 = (int) axisOrder.getSecond().getAlong(chunkPos);
        int i2 = (int) axisOrder.getThird().getAlong(chunkPos);

        int index = i0 + (i1 * SIZE) + (i2 * SIZE * SIZE);
        updateMaskAndShift(index);

        int count = countBits(mask, index) - 1;
        return setPointer(
                placement,
                i0,
                i1,
                i2,
                count
        );
    }

    /**
     * Removes a chunk pointer and updates the mask for the specified count and index.
     * @param count The pointer array index.
     * @param index The block index.
     */
    private void removeChunk(int count, int index) {
        Pointer[] clone = this.pointers;
        this.pointers = new Pointer[this.pointers.length - 1];
        if (count == 0) {
            System.arraycopy(clone, 1, this.pointers, 0, this.pointers.length);
        } else if (count >= this.pointers.length) {
            System.arraycopy(clone, 0, this.pointers, 0, this.pointers.length);
        } else {
            System.arraycopy(clone, 0, this.pointers, 0, count);

            if (this.pointers.length - count >= 0)
                System.arraycopy(clone, count + 1, this.pointers, count, this.pointers.length - count);
        }

        long prev = mask;

        mask &= ~(1L << index);

        if ((mask | (1L << index)) != prev)
            throw new IllegalStateException("why: " + prev + " & " + mask + " | " + (1L << index) + " > " + index);

        if (((mask >> index) & 1) != 0)
            throw new IllegalStateException("Something went wrong:\nmask: " + mask + "\nindex: " + index);

    }

    /**
     * Removes a block at the specified position.
     * @param pos The position to remove the block from.
     * @return The removed BlockData, or null if not present.
     */
    @Override
    public @Nullable BlockData removeBlock(Position pos) {
        OptionalInt optIndex = getIndex(pos);

        if (optIndex.isEmpty())
            return null;

        OptionalInt optCount = getCountBits(optIndex.getAsInt());

        if (optCount.isEmpty())
            return null;

        int index = optIndex.getAsInt();
        int count = optCount.getAsInt();

        Pointer pointer = this.pointers[count];
        Ensures.notNull(pointer, "Something went wrong with pointers");

        if (scaler == 1) {
            removeChunk(count, index);
            return ((BlockPointer) pointer).getData();
        }

        if (!(pointer instanceof ChunkPointer chunkPointer))
            return null;

        if (!(chunkPointer.getChunk() instanceof MutableBlockChunk chunk))
            throw new IllegalArgumentException("Unexpected chunk type: " + chunkPointer.getChunk().getClass().getSimpleName());

        BlockData blockData = chunk.removeBlock(pos);
        if (chunkPointer.getChunk().getMask() == 0)
            removeChunk(count, index);

        return blockData;
    }

    /**
     * Returns a mutable copy of this chunk.
     * @return A new MutableBlockChunkImpl instance.
     */
    @Override
    public MutableBlockChunk mutable() {
        return new MutableBlockChunkImpl(this);
    }

    /**
     * Returns an immutable copy of this chunk.
     * @return A new BlockChunkImpl instance.
     */
    @Override
    public BlockChunk immutable() {
        return new BlockChunkImpl(this);
    }
}
