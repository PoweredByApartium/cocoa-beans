package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.axis.Axis;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.structs.Entry;

import java.time.Instant;
import java.util.Iterator;
import java.util.UUID;

public interface Schematic {

    UUID id();

    Instant created();

    String author();
    String title();

    Position offset();
    Dimensions size();
    AxisOrder axisOrder();

    BlockData getBlockData(int x, int y, int z);
    Iterator<Entry<Position, BlockData>> blocksIterator();

    Schematic rotate(int rotate);
    Schematic flip(Axis axis);

    Schematic translate(Position offset);
    Schematic translate(AxisOrder axisOrder);

    Schematic setBlock(int x, int y, int z, BlockData data);
    Schematic removeBlock(int x, int y, int z);

}