package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

/**
 * Builder for {@link SpigotSchematic} instances.
 * <p>
 * This class provides a fluent API to construct Spigot-specific schematic objects, allowing customization of metadata,
 * platform, offset, size, axes, and block chunk data. It extends {@link AbstractSchematicBuilder} and adapts the
 * schematic building process for the Spigot platform.
 * <p>
 * Usage example:
 * <pre>
 *   SpigotSchematic schematic = new SpigotSchematicBuilder()
 *       .platform(...)
 *       .offset(...)
 *       .size(...)
 *       .axes(...)
 *       .metadata(builder -> builder.name("Example").build())
 *       .build();
 * </pre>
 *
 * @since 0.0.46
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicBuilder extends AbstractSchematicBuilder<SpigotSchematicBuilder> {

    /**
     * Constructs a new empty SpigotSchematicBuilder.
     */
    public SpigotSchematicBuilder() {
        super();
    }

    /**
     * Constructs a SpigotSchematicBuilder initialized with an existing schematic.
     *
     * @param schematic the schematic to initialize from
     */
    public SpigotSchematicBuilder(Schematic schematic) {
        super(schematic);
    }

    /**
     * Sets the schematic metadata using a builder function.
     *
     * @param block function to build schematic metadata
     * @return this builder instance
     */
    @Override
    public SpigotSchematicBuilder metadata(Function<SchematicMetadataBuilder, SchematicMetadata> block) {
        return metadata(block.apply(SchematicMetadata.builder()));
    }

    /**
     * Builds and returns the {@link SpigotSchematic} instance.
     *
     * @return the constructed SpigotSchematic
     */
    @Override
    public SpigotSchematic build() {
        return new SpigotSchematic(
                platform,
                created,
                metadata == null ? SchematicMetadata.of() : metadata,
                offset,
                size,
                axes,
                new BlockChunkIterator(blockChunk)
        );
    }

    @Override
    protected SpigotSchematicBuilder self() {
        return this;
    }

}
