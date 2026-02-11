package net.apartium.cocoabeans.schematic.iterator;

import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import static net.apartium.cocoabeans.schematic.block.BlockChunkImpl.SIZE;

@ApiStatus.AvailableSince("0.0.46")
public class BlockChunkIterator implements BlockIterator {

    private final long mask;
    private final List<Pointer> pointers;
    private final Position actualPos;
    private final AxisOrder axisOrder;
    private long remaining;
    private int pointerIdx = 0;

    private BlockChunkIterator child = null;
    private BlockPlacement next;

    public BlockChunkIterator(BlockChunk chunk) {
        this.mask = chunk.getMask();
        this.remaining = chunk.getMask();
        this.pointers = chunk.getPointers();
        this.actualPos = chunk.getActualPos();

        this.axisOrder = chunk.getAxisOrder();

        this.advance();
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
            int bitPos = Long.numberOfTrailingZeros(remaining);
            long bit = 1L << bitPos;

            remaining ^= bit;
            if (pointerIdx >= pointers.size())
                throw new IllegalStateException("Mask has more set bits than pointers length\nMask: " + mask + "\nRemaining: " + remaining + "\nBitPos: " + bitPos);

            Pointer ptr = pointers.get(pointerIdx++);
            if (ptr == null)
                throw new NullPointerException("pointer is null at compact index " + (pointerIdx - 1));

            if (ptr instanceof BlockPointer blockPointer) {
                int i0 = bitPos % SIZE;
                int i1 = (bitPos / SIZE) % SIZE;
                int i2 = bitPos / (SIZE * SIZE);

                Position pos = axisOrder.position(i0, i1, i2).add(actualPos);
                next = new BlockPlacement(pos, blockPointer.getData());
                return;
            }

            if (ptr instanceof ChunkPointer chunkPointer) {
                child = new BlockChunkIterator(chunkPointer.getChunk());
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

        if (pointerIdx != pointers.size())
            throw new IllegalStateException("pointers length (" + pointers.size() + ") does not match popcount(mask) (" + pointerIdx + ")");


        next = null;
    }

    @Override
    public Position current() {
        if (next == null)
            return null;

        return next.position();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public BlockPlacement next() {
        BlockPlacement placement = next;
        advance();
        return placement;
    }

}
