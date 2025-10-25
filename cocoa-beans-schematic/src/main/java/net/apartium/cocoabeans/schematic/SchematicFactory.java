package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;
import java.util.UUID;

@ApiStatus.AvailableSince("0.0.45")
public interface SchematicFactory<S extends Schematic> {

    S createSchematic(UUID id, Instant created, MinecraftPlatform platform, String author, String title, BlockIterator blocks, Dimensions size, AxisOrder axisOrder, Position offset);

}
