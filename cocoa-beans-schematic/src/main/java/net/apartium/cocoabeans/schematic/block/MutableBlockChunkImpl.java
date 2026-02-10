package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class MutableBlockChunkImpl extends BlockChunkImpl implements MutableBlockChunk {

    public MutableBlockChunkImpl(@NonNull AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, BlockChunk prev) {
        super(axisOrder, scaler, actualPos, chunkPos, prev);
    }

    public MutableBlockChunkImpl(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos) {
        super(axisOrder, scaler, actualPos, chunkPos);
    }

    public MutableBlockChunkImpl(BlockChunk chunk) {
        super(chunk);
    }

    @Override
    protected BlockChunk create(BlockChunk blockChunk) {
        return new MutableBlockChunkImpl(blockChunk);
    }

    @Override
    protected BlockChunk create(@NonNull AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, BlockChunk prev) {
        return new MutableBlockChunkImpl(axisOrder, scaler, actualPos, chunkPos, prev);
    }

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
                throw new RuntimeException("Unexpected chunk type: " + chunk.getClass().getSimpleName());

            return mutableChunk.setBlock(placement);
        }

        return false;
    }

    @Override
    public boolean setBlock(BlockPlacement placement) {
        Position pos = placement.position();

        if (axisOrder.compare(pos, actualPos) < 0)
            return false;

        Position chunkPos = new Position(pos).subtract(actualPos).divide(scaler).floor();
        if (chunkPos.getX() >= SIZE ||  chunkPos.getY() >= SIZE || chunkPos.getZ() >= SIZE)
            throw new IllegalStateException("TF: (" + chunkPos + ") (" + pos + ")");

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

    @Override
    public @Nullable BlockData removeBlock(Position pos) {
        if (axisOrder.compare(pos, actualPos) < 0)
            return null;

        Position chunkPos = new Position(pos).subtract(actualPos).divide(scaler).floor();
        if (chunkPos.getX() >= SIZE ||  chunkPos.getY() >= SIZE || chunkPos.getZ() >= SIZE)
            throw new IllegalStateException("TF: (" + chunkPos + ") (" + pos + ")");

        int i0 = (int) axisOrder.getFirst().getAlong(chunkPos);
        int i1 = (int) axisOrder.getSecond().getAlong(chunkPos);
        int i2 = (int) axisOrder.getThird().getAlong(chunkPos);

        int index = i0 + (i1 * SIZE) + (i2 * SIZE * SIZE);
        if (((mask >> index) & 1) == 0)
            return null;


        int count = countBits(mask, index) - 1;
        Pointer pointer = this.pointers[count];

        if (scaler == 1) {
            removeChunk(count, index);
            return ((BlockPointer) pointer).getData();
        }

        if (!(pointer instanceof ChunkPointer chunkPointer))
            return null;

        if (!(chunkPointer.getChunk() instanceof MutableBlockChunk chunk))
            throw new RuntimeException("Unexpected chunk type: " + chunkPointer.getChunk().getClass().getSimpleName());

        BlockData blockData = chunk.removeBlock(pos);
        if (chunkPointer.getChunk().getMask() == 0)
            removeChunk(count, index);

        return blockData;
    }

    @Override
    public MutableBlockChunk mutable() {
        return new MutableBlockChunkImpl(this);
    }

    @Override
    public BlockChunk immutable() {
        return new BlockChunkImpl(this);
    }
}
