package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicFactory implements SchematicFactory<SpigotSchematic> {

    @Override
    public SpigotSchematicBuilder createSchematic() {
        return new SpigotSchematicBuilder();
    }

    @Override
    public SpigotSchematic createSchematic(Instant created, MinecraftPlatform platform, SchematicMetadata metadata,
                                           BlockIterator blocks, AreaSize size, AxisOrder axisOrder, Position offset) {
        SpigotSchematicBuilder builder = new SpigotSchematicBuilder();

        builder
                .created(created)
                .platform(platform)
                .metadata(metadata)
                .translate(axisOrder)
                .translate(offset)
                .size(size);

        while (blocks.hasNext()) {
            BlockPlacement placement = blocks.next();
            Position position = placement.position();
            BlockData data = placement.block();

            builder.setBlock((int) position.getX(), (int) position.getY(), (int) position.getZ(), data);
        }

        return builder.build();
    }

    @Override
    public SchematicMetadata createMetadata(Map<String, Object> metadata) {
        return new SpigotSchematicMetadata(metadata);
    }

}
