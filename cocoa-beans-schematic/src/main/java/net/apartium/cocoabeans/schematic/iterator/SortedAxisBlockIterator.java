package net.apartium.cocoabeans.schematic.iterator;

import net.apartium.cocoabeans.schematic.block.BlockChunk;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.Set;

@ApiStatus.AvailableSince("0.0.46")
public class SortedAxisBlockIterator implements BlockIterator {

    private final BlockChunk chunk;
    private final Iterator<Position> positionIterator;
    private final AxisOrder axisOrder;
    private BlockPlacement next;

    public SortedAxisBlockIterator(BlockChunk chunk, AreaSize size, AxisOrder axisOrder) {
        this(chunk, size, axisOrder, Set.of());
    }

    public SortedAxisBlockIterator(BlockChunk chunk, AreaSize size, AxisOrder axisOrder, Set<Axis> reverseAxis) {
        this.chunk = chunk;
        this.axisOrder = axisOrder;

        double x = size.width() - 1;
        double y = size.height() - 1;
        double z = size.depth() - 1;

        Position pos0 = new Position(
                reverseAxis.contains(Axis.X) ? x : 0,
                reverseAxis.contains(Axis.Y) ? y : 0,
                reverseAxis.contains(Axis.Z) ? z : 0
        );

        Position pos1 = new Position(
                reverseAxis.contains(Axis.X) ? 0 : x,
                reverseAxis.contains(Axis.Y) ? 0 : y,
                reverseAxis.contains(Axis.Z) ? 0 : z
        );

        this.positionIterator = axisOrder.iterator(pos0, pos1, 1);

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
