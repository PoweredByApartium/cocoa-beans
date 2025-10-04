package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface SchematicFactory<S extends Schematic> {

    S createSchematic();
    S createSchematic(UUID id, Instant created, String author, String title, Map<Position, BlockData> blocks, Dimensions size, AxisOrder axisOrder, Position offset);

}
