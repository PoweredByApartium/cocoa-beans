package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;

import java.util.Arrays;

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
        BlockData data = getBlock(pos);

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
                throw new IllegalStateException("FUCK");
        }

        int count = countBits(mask, index) - 1;
        Pointer pointer = pointers[count];
        if (pointer == null) {
            if (scaler == 1) {
                pointer = new BlockPointer(placement.block());
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

            pointers[count] = new BlockPointer(blockPointer.getData());
            return true;
        } else if (pointer instanceof ChunkPointer chunkPointer) {
            return chunkPointer.getChunk().setBlock(placement);
        }

        return false;
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
}
