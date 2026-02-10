package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Represents a batch of blocks in a schematic
 * @see BlockData
 */
@ApiStatus.AvailableSince("0.0.46")
public class BlockChunkImpl implements BlockChunk {

    public static final int SIZE = 4;

    /* package-private */ final AxisOrder axisOrder;
    /* package-private */ final double scaler;
    /* package-private */ final double nextScaler;
    /* package-private */ final Position actualPos;
    /* package-private */ final Position chunkPos;
    /* package-private */ Pointer[] pointers = new Pointer[0];
    /* package-private */ long mask = 0;

    public BlockChunkImpl(BlockChunk chunk) {
        this.axisOrder = chunk.getAxisOrder();
        this.scaler = chunk.getScaler();
        this.nextScaler = Math.floor(scaler / SIZE);
        this.actualPos = chunk.getActualPos();
        this.chunkPos = chunk.getChunkPos();

        // Copy pointers as immutable
        this.mask = chunk.getMask();

        Pointer[] chunkPointers = chunk.getPointers();
        this.pointers = new Pointer[chunkPointers.length];

        for (int i = 0; i < chunkPointers.length; i++) {
            Pointer pointer = chunkPointers[i];
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

    protected BlockChunkImpl(@NonNull AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, BlockChunk prev) {
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

    public BlockChunkImpl(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos) {
        this(axisOrder, scaler, actualPos, chunkPos, null);
    }

    protected BlockChunk create(BlockChunk blockChunk) {
        return new BlockChunkImpl(blockChunk);
    }

    protected BlockChunk create(@NonNull AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, BlockChunk prev) {
        return new BlockChunkImpl(
                axisOrder,
                scaler,
                actualPos,
                chunkPos,
                prev
        );
    }

    @Override
    public BlockData getBlock(Position pos) {
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
        Pointer pointer = pointers[count];
        if (pointer instanceof BlockPointer blockPointer)
            return blockPointer.getData();

        if (pointer instanceof ChunkPointer chunkPointer)
            return chunkPointer.getChunk().getBlock(pos);

        throw new UnsupportedOperationException("Not supported yet: " + pointer.getClass().getName());
    }


    static int countBits(long mask, int index) {
        int count = 0;
        for (int i = index; i >= 0; i--) {
            if ((mask & (1L << i)) != 0) {
                count++;
            }
        }

        return count;
    }

    // todo make array
    @Override
    public Pointer[] getPointers() {
        return Arrays.copyOf(pointers, pointers.length);
    }

    @Override
    public long getMask() {
        return mask;
    }

    @Override
    public double getScaler() {
        return scaler;
    }

    @Override
    public Position getActualPos() {
        return actualPos;
    }

    @Override
    public Position getChunkPos() {
        return chunkPos;
    }

    @Override
    public AxisOrder getAxisOrder() {
        return axisOrder;
    }

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

    @Override
    public MutableBlockChunk mutable() {
        return new MutableBlockChunkImpl(this);
    }

    @Override
    public BlockChunk immutable() {
        return this;
    }
}
