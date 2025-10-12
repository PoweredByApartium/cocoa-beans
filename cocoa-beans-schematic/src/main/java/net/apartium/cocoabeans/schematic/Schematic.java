package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.axis.Axis;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;
import java.util.Iterator;
import java.util.UUID;

@ApiStatus.AvailableSince("0.0.45")
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

    SchematicBuilder builder();

}