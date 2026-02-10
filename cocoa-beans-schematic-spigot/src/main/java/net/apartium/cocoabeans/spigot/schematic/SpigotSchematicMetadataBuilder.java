package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadataBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for {@link SpigotSchematicMetadata} objects.
 * <p>
 * This class allows for the construction of schematic metadata specific to the Spigot platform.
 * It extends {@link AbstractSchematicMetadataBuilder} and provides methods to build metadata
 * using either an empty map or a pre-existing map.
 * </p>
 *
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicMetadataBuilder extends AbstractSchematicMetadataBuilder<SpigotSchematicMetadata> {

    /**
     * Constructs a new builder with an empty metadata map.
     */
    public SpigotSchematicMetadataBuilder() {
        super(new HashMap<>());
    }

    /**
     * Constructs a new builder with the provided metadata map.
     *
     * @param metadata the initial metadata map
     */
    public SpigotSchematicMetadataBuilder(Map<String, Object> metadata) {
        super(metadata);
    }

    /**
     * Builds a {@link SpigotSchematicMetadata} instance from the current metadata.
     *
     * @return a new {@link SpigotSchematicMetadata} instance
     */
    @Override
    public SpigotSchematicMetadata build() {
        return new SpigotSchematicMetadata(this.metadata);
    }

}
