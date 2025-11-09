package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class SpigotSchematicFactory implements SchematicFactory<SpigotSchematic> {

    @Override
    public SpigotSchematic createSchematic(Instant created, MinecraftPlatform platform, SchematicMetadata metadata, BlockIterator blocks, AreaSize size, AxisOrder axisOrder, Position offset) {
        SpigotSchematicBuilder builder = new SpigotSchematicBuilder();

        builder.created(created);
        builder.platform(platform);
        builder.metadata(metadata);

        builder.translate(axisOrder);
        builder.translate(offset);
        builder.size(size);

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
