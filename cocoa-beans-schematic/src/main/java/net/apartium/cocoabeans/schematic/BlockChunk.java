package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BlockChunk implements Iterable<Entry<Position, BlockData>> {

    public static final int SIZE = 4;

    private final AxisOrder axisOrder;
    private final double scaler;
    private final double nextScaler;
    private final Position actualPos;
    private final Position chunkPos;
    private Pointer[] pointers = new Pointer[0];
    private long mask = 0;

    public BlockChunk(AxisOrder axisOrder, double scaler, Position actualPos, Position chunkPos) {
        this.axisOrder = axisOrder;
        this.scaler = scaler;
        this.nextScaler = Math.floor(scaler / SIZE);
        this.actualPos = actualPos;
        this.chunkPos = chunkPos;
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

    public boolean setBlock(Position pos, BlockData data) {
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
            if (scaler == 1)
                pointer = new BlockPointer(data);
            else {
                Position chunkPoint = axisOrder.position(i0, i1, i2);
                pointer = new ChunkPointer(new BlockChunk(
                        axisOrder,
                        nextScaler,
                        new Position(actualPos).add(new Position(chunkPoint).multiply(scaler)),
                        chunkPoint
                ));
                ((ChunkPointer) pointer).getChunk().setBlock(pos, data);
            }

            pointers[count] = pointer;
            return true;

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

    private static int countBits(long mask, int index) {
        int count = 0;
        for (int i = index; i >= 0; i--) {
            if ((mask & (1L << i)) != 0) {
                count++;
            }
        }

        return count;
    }

    @Override
    public @NotNull Iterator<Entry<Position, BlockData>> iterator() {
        return new Iterator<>() {

            private long remaining = mask;
            private int pointerIdx = 0;
            private Iterator<Entry<Position, BlockData>> child = null;
            private Entry<Position, BlockData> next;

            {
                advance();
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Entry<Position, BlockData> next() {
                if (next == null)
                    throw new NoSuchElementException();

                Entry<Position, BlockData> out = next;
                advance();
                return out;
            }

            private void advance() {
                if (child != null) {
                    if (child.hasNext()) {
                        next = child.next();
                        if (!child.hasNext())
                            child = null;

                        return;
                    } else {
                        child = null;
                    }
                }

                while (remaining != 0) {
                    int bitOffset = Long.numberOfTrailingZeros(remaining);
                    int bitPos = bitOffset;
                    long bit = 1L << bitPos;

                    remaining ^= bit;
                    if (pointerIdx >= pointers.length)
                        throw new IllegalStateException("Mask has more set bits than pointers length");

                    Pointer ptr = pointers[pointerIdx++];
                    if (ptr == null)
                        throw new NullPointerException("pointer is null at compact index " + (pointerIdx - 1));

                    if (ptr instanceof BlockPointer blockPointer) {
                        int i0 = bitPos % SIZE;
                        int i1 = (bitPos / SIZE) % SIZE;
                        int i2 = bitPos / (SIZE * SIZE);

                        Position pos = new Position(i0, i1, i2).add(actualPos);
                        next = new Entry<>(pos, blockPointer.getData());
                        return;
                    }

                    if (ptr instanceof ChunkPointer chunkPointer) {
                        child = chunkPointer.getChunk().iterator();
                        if (child.hasNext()) {
                            next = child.next();
                            if (!child.hasNext())
                                child = null;

                            return;
                        } else {
                            child = null;
                            continue;
                        }
                    }

                    throw new UnsupportedOperationException("Not supported: " + ptr.getClass().getName());
                }

                if (pointerIdx != pointers.length)
                    throw new IllegalStateException("pointers length (" + pointers.length + ") does not match popcount(mask) (" + pointers.length + ")");


                next = null;
            }

        };
    }

    public Pointer[] getPointers() {
        return Arrays.copyOf(pointers, pointers.length);
    }

    public long getMask() {
        return mask;
    }
}
