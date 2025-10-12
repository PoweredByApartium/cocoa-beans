package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ApiStatus.AvailableSince("0.0.45")
public interface SchematicFactory<S extends Schematic> {

    S createSchematic(UUID id, Instant created, String author, String title, Map<Position, BlockData> blocks, Dimensions size, AxisOrder axisOrder, Position offset);

}
