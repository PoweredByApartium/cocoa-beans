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
        this(chunk.getAxisOrder(), chunk.getScaler(), chunk.getActualPos(), chunk.getChunkPos(), chunk);
    }

    @Override
    protected BlockChunk create(@NonNull AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, BlockChunk prev) {
        return new MutableBlockChunkImpl(axisOrder, scaler, actualPos, chunkPos, prev);
    }

    @Override
    public boolean setBlock(BlockPlacement placement) {
        Position pos = placement.position();
        BlockData data = placement.block();

        if (axisOrder.compare(pos, actualPos) < 0)
            return false;

        Position chunkPos = new Position(pos).subtract(actualPos).divide(scaler).floor();
        if (chunkPos.getX() >= SIZE ||  chunkPos.getY() >= SIZE || chunkPos.getZ() >= SIZE)
            throw new IllegalStateException("TF: (" + chunkPos + ") (" + pos + ")");

        int i0 = (int) axisOrder.getFirst().getAlong(chunkPos);
        int i1 = (int) axisOrder.getSecond().getAlong(chunkPos);
        int i2 = (int) axisOrder.getThird().getAlong(chunkPos);

        int index = i0 + (i1 * SIZE) + (i2 * SIZE * SIZE);
        if (((mask >> index) & 1) == 0) {
            // UPDATE MASK and shift array
            int count = countBits(mask, index - 1);
            if (count == 0) {
                if (pointers.length == 0) {
                    pointers = new Pointer[1];
                } else {
                    Pointer[] clone = pointers;
                    pointers = new Pointer[pointers.length + 1];
                    System.arraycopy(clone, 0, pointers, 1, clone.length);
                }
            } else {
                if (pointers.length == count) {
                    Pointer[] clone = pointers;
                    pointers = new Pointer[pointers.length + 1];
                    System.arraycopy(clone, 0, pointers, 0, clone.length);
                } else {
                    Pointer[] clone = pointers;
                    pointers = new Pointer[pointers.length + 1];
                    for (int i = 0; i < count; i++) {
                        pointers[i] = clone[i];
                    }


                    for (int i = count + 1; i < pointers.length; i++) {
                        pointers[i] = clone[i - 1];
                    }
                }
            }

            mask |= (1L << index);
            if (((mask >> index) & 1) == 0)
                throw new IllegalStateException("Something went wrong:\nmask: " + mask + "\nindex: " + index);
        }

        int count = countBits(mask, index) - 1;
        Pointer pointer = pointers[count];
        if (pointer == null) {
            if (scaler == 1) {
                pointer = new BlockPointer(data);
            }
            else {
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

        } else if (pointer instanceof BlockPointer blockPointer) {
            if (blockPointer.getData().equals(data))
                return true;

            pointers[count] = new BlockPointer(data);
            return true;
        } else if (pointer instanceof ChunkPointer chunkPointer) {
            BlockChunk chunk = chunkPointer.getChunk();
            if (!(chunk instanceof MutableBlockChunk mutableChunk))
                throw new RuntimeException("Unexpected chunk type: " + chunk.getClass().getSimpleName());

            return mutableChunk.setBlock(placement);
        }

        return false;
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
        return this;
    }

    @Override
    public BlockChunk immutable() {
        return new BlockChunkImpl(this);
    }
}
