package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;

/* package-private */ class BlockChunk {

    public static final int SIZE = 4;

    private final AxisOrder axisOrder;
    private final double scaler;
    private final double nextScaler;
    private final Position actualPos;
    private final Position chunkPos;
    private Pointer[] pointers = new Pointer[0];
    private long mask = 0;

    BlockChunk(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos) {
        this.axisOrder = axisOrder;
        this.scaler = scaler;
        this.nextScaler = Math.floor(scaler / SIZE);
        this.actualPos = actualPos;
        this.chunkPos = chunkPos;
    }

    public BlockData getBlock(Position pos) {
        if (axisOrder.compare(pos, actualPos) > 0)
            return null;

        Position otherChunkPoint = new Position(pos).divide(scaler).floor();
        if (!this.chunkPos.equals(otherChunkPoint))
            return null;

        Position chunkPos = new Position(otherChunkPoint).subtract(actualPos);
        if (chunkPos.getX() <= SIZE ||  chunkPos.getY() <= SIZE || chunkPos.getZ() <= SIZE)
            throw new IllegalStateException("TF");

        int i0 = (int) axisOrder.getFirst().getAlong(chunkPos);
        int i1 = (int) axisOrder.getSecond().getAlong(chunkPos);
        int i2 = (int) axisOrder.getThird().getAlong(chunkPos);

        int index = i0 + (i1 * SIZE) + (i2 * SIZE * SIZE);
        if ((mask >> index & 1) == 0)
            return null;

        int count = Long.bitCount((mask & ((1L << index) - 1)));
        return switch (pointers[count]) {
            case BlockPointer blockPointer -> blockPointer.getData();
            case ChunkPointer chunkPointer -> chunkPointer.getChunk().getBlock(pos);
        };
    }

    public boolean setBlock(Position pos, BlockData data) {
        if (axisOrder.compare(pos, actualPos) > 0)
            return false;

        Position otherChunkPoint = new Position(pos).divide(scaler).floor();
        if (!this.chunkPos.equals(otherChunkPoint))
            return false;

        Position chunkPos = new Position(otherChunkPoint).subtract(actualPos);
        if (chunkPos.getX() <= SIZE ||  chunkPos.getY() <= SIZE || chunkPos.getZ() <= SIZE)
            throw new IllegalStateException("TF");

        int i0 = (int) axisOrder.getFirst().getAlong(chunkPos);
        int i1 = (int) axisOrder.getSecond().getAlong(chunkPos);
        int i2 = (int) axisOrder.getThird().getAlong(chunkPos);

        int index = i0 + (i1 * SIZE) + (i2 * SIZE * SIZE);
        if ((mask >> index & 1) == 0) {

            // UPDATE MASK and shift array
            int count = Long.bitCount((mask & ((1L << index) - 1)));
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
                    Pointer[] clone = new Pointer[pointers.length + 1];
                    System.arraycopy(clone, 0, pointers, 0, count);

                    for (int i = count + 1; i < pointers.length; i++) {
                        pointers[i] = clone[i - 1];
                    }
                }
            }

            mask |= (1L << index);
        }

        int count = Long.bitCount((mask & ((1L << index) - 1)));
        Pointer pointer = pointers[count];
        if (pointer == null) {
            if (scaler == 1)
                pointer = new BlockPointer(data);
            else {
                Position chunkPoint = axisOrder.position(i0, i1, i2);
                pointer = new ChunkPointer(new BlockChunk(
                        axisOrder,
                        nextScaler,
                        new Position(actualPos).add(chunkPoint.multiply(scaler)),
                        chunkPoint
                ));
            }

            pointers[count] = pointer;
        } else if (pointer instanceof BlockPointer blockPointer) {
            if (blockPointer.getData().equals(data))
                return true;

            pointers[count] = new BlockPointer(blockPointer.getData());
            return true;
        } else if (pointer instanceof ChunkPointer chunkPointer) {
            return chunkPointer.getChunk().setBlock(pos, data);
        }

        return false;
    }


}
