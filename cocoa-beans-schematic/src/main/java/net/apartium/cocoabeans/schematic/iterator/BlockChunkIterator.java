package net.apartium.cocoabeans.schematic.iterator;

import net.apartium.cocoabeans.schematic.block.BlockChunk;
import net.apartium.cocoabeans.schematic.block.BlockPointer;
import net.apartium.cocoabeans.schematic.block.ChunkPointer;
import net.apartium.cocoabeans.schematic.block.Pointer;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.Position;

import static net.apartium.cocoabeans.schematic.block.BlockChunk.SIZE;

public class BlockChunkIterator implements BlockIterator {

    private final Pointer[] pointers;
    private final Position actualPos;
    private long remaining;
    private int pointerIdx = 0;

    private BlockChunkIterator child = null;
    private BlockPlacement next;

    public BlockChunkIterator(BlockChunk chunk) {
        this.remaining = chunk.getMask();
        this.pointers = chunk.getPointers();
        this.actualPos = chunk.getActualPos();

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

        if (pointerIdx != pointers.length)
            throw new IllegalStateException("pointers length (" + pointers.length + ") does not match popcount(mask) (" + pointers.length + ")");


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
