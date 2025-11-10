package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@ApiStatus.AvailableSince("0.0.46")
public class BlockChunk {

    public static final int SIZE = 4;

    private final AxisOrder axisOrder;
    private final double scaler;
    private final double nextScaler;
    private final Position actualPos;
    private final Position chunkPos;
    private Pointer[] pointers = new Pointer[0];
    private long mask = 0;

    public BlockChunk(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos, BlockChunk prev) {
        this.axisOrder = axisOrder;
        this.scaler = scaler;
        this.nextScaler = Math.floor(scaler / SIZE);
        this.actualPos = actualPos;
        this.chunkPos = chunkPos;
        if (prev != null) {
            mask = 1;
            if (prev.getScaler() != nextScaler) {
                pointers = new Pointer[]{new ChunkPointer(new BlockChunk(axisOrder, nextScaler, actualPos, chunkPos, prev))};
            } else {
                pointers = new Pointer[]{new ChunkPointer(prev)};
            }
        }
    }

    public BlockChunk(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos) {
        this(axisOrder, scaler, actualPos, chunkPos, null);
    }

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
                pointer = new ChunkPointer(new BlockChunk(
                        axisOrder,
                        nextScaler,
                        new Position(actualPos).add(new Position(chunkPoint).multiply(scaler)),
                        chunkPoint
                ));
                ((ChunkPointer) pointer).getChunk().setBlock(placement);
            }

            pointers[count] = pointer;
            return true;

        } else if (pointer instanceof BlockPointer blockPointer) {
            if (blockPointer.getData().equals(data))
                return true;

            pointers[count] = new BlockPointer(data);
            return true;
        } else if (pointer instanceof ChunkPointer chunkPointer) {
            return chunkPointer.getChunk().setBlock(placement);
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
            for (int i = 0; i < count; i++)
                this.pointers[i] = clone[i];

            for (int i = count; i < this.pointers.length; i++) {
                this.pointers[i] = clone[i + 1];
            }
        }

        long prev = mask;

        mask &= ~(1L << index);

        if ((mask | (1L << index)) != prev)
            throw new IllegalStateException("why: " + prev + " & " + mask + " | " + (1L << index) + " > " + index);

        if (((mask >> index) & 1) != 0)
            throw new IllegalStateException("Something went wrong:\nmask: " + mask + "\nindex: " + index);

    }

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

        BlockData blockData = chunkPointer.getChunk().removeBlock(pos);
        if (chunkPointer.getChunk().getMask() == 0)
            removeChunk(count, index);

        return blockData;
    }

    private static int countBits(long mask, int index) {
        int count = 0;
        for (int i = index; i >= 0; i--) {
            if ((mask & (1L << i)) != 0) {
                count++;
            }
        }

        return count;
    }

    public Pointer[] getPointers() {
        return Arrays.copyOf(pointers, pointers.length);
    }

    public long getMask() {
        return mask;
    }

    public double getScaler() {
        return scaler;
    }

    public Position getActualPos() {
        return actualPos;
    }

    public Position getChunkPos() {
        return chunkPos;
    }

    public AxisOrder getAxisOrder() {
        return axisOrder;
    }

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

    public static boolean[] debug(long num) {
        boolean[] arr = new boolean[64];
        for (int i = 0; i < 64; i++)
            arr[i] = ((num >>> (63- i)) & 1L) != 0;

        return arr;
    }

}
