package net.apartium.cocoabeans.schematic.iterator;

import net.apartium.cocoabeans.schematic.block.BlockChunk;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;

@ApiStatus.AvailableSince("0.0.46")
public class SortedAxisBlockIterator implements BlockIterator {

    private final BlockChunk chunk;
    private final Iterator<Position> positionIterator;
    private final AxisOrder axisOrder;
    private BlockPlacement next;

    public SortedAxisBlockIterator(BlockChunk chunk, AreaSize size, AxisOrder axisOrder) {
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
