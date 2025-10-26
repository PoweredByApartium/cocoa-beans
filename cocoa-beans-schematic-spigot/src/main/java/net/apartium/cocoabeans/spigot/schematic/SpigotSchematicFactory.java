package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.UUID;

public class SpigotSchematicFactory implements SchematicFactory<SpigotSchematic> {
    @Override
    public SpigotSchematic createSchematic(UUID id, Instant created, MinecraftPlatform platform, String author, String title, BlockIterator blocks, Dimensions size, AxisOrder axisOrder, Position offset) {
        SpigotSchematicBuilder builder = new SpigotSchematic().toBuilder();

        builder.id(id);
        builder.created(created);
        builder.platform(platform);
        builder.author(author);
        builder.title(title);

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

}
