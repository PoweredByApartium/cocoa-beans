package net.apartium.cocoabeans.schematic.iterator;

import net.apartium.cocoabeans.schematic.BlockChunk;
import net.apartium.cocoabeans.schematic.BlockData;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.Position;

import java.util.Iterator;

public class SortedAxisBlockIterator implements BlockIterator {

    private final BlockChunk chunk;
    private final Iterator<Position> positionIterator;
    private final AxisOrder axisOrder;
    private BlockPlacement next;

    public SortedAxisBlockIterator(BlockChunk chunk, Dimensions size, AxisOrder axisOrder) {
        this.chunk = chunk;
        this.axisOrder = axisOrder;
        this.positionIterator = axisOrder.iterator(
                new Position(0, 0, 0),
                new Position(size.width() - 1, size.height() - 1, size.depth() - 1),
                1
        );

        this.advance();
    }

    private void advance() {
        next = null;
        while (positionIterator.hasNext() && next == null) {
            Position position = positionIterator.next();

            BlockData block = chunk.getBlock(position);
            if (block == null)
                continue;

            this.next = new BlockPlacement(position, block);
        }
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

    public AxisOrder axisOrder() {
        return axisOrder;
    }


}
