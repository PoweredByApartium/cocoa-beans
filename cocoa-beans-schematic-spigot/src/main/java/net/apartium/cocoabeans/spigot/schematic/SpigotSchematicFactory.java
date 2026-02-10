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
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.Map;

/**
 * Factory for creating {@link SpigotSchematic} instances and related metadata.
 * <p>
 * Implements the {@link SchematicFactory} interface for the Spigot platform.
 * </p>
 *
 * @since 0.0.46
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicFactory implements SchematicFactory<SpigotSchematic> {

    /**
     * Creates a new {@link SpigotSchematicBuilder} for building schematics.
     *
     * @return a new builder instance
     */
    @Override
    public SpigotSchematicBuilder createSchematic() {
        return new SpigotSchematicBuilder();
    }

    /**
     * Constructs a {@link SpigotSchematic} from the provided parameters.
     *
     * @param created  the creation timestamp
     * @param platform the Minecraft platform
     * @param metadata schematic metadata
     * @param blocks   iterator over block placements
     * @param size     the schematic area size
     * @param axisOrder the axis order for translation
     * @param offset   the position offset
     * @return a built {@link SpigotSchematic}
     */
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

    /**
     * Creates schematic metadata for Spigot from a map.
     *
     * @param metadata the metadata map
     * @return a {@link SpigotSchematicMetadata} instance
     */
    @Override
    public SchematicMetadata createMetadata(Map<String, Object> metadata) {
        return new SpigotSchematicMetadata(metadata);
    }

}
