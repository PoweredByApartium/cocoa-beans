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

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicFactory implements SchematicFactory<SpigotSchematic> {

    @Override
    public @NotNull SpigotSchematicBuilder createSchematic() {
        return new SpigotSchematicBuilder();
    }

    @Override
    public SpigotSchematic createSchematic(@NotNull Instant created, @NotNull MinecraftPlatform platform, @NotNull SchematicMetadata metadata,
                                           BlockIterator blocks, @NotNull AreaSize size, @NotNull AxisOrder axisOrder, @NotNull Position offset) {
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
    public @NotNull SchematicMetadata createMetadata(@NotNull Map<String, Object> metadata) {
        return new SpigotSchematicMetadata(metadata);
    }

}
