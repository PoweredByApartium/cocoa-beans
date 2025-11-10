package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ApiStatus.AvailableSince("0.0.46")
public interface SchematicFactory<S extends Schematic> {

    S createSchematic(Instant created, MinecraftPlatform platform, SchematicMetadata metadata, BlockIterator blocks, AreaSize size, AxisOrder axisOrder, Position offset);
    SchematicMetadata createMetadata(Map<String, Object> metadata);

}
