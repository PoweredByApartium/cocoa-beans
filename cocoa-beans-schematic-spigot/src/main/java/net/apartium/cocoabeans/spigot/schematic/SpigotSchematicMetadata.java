package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents schematic metadata for Spigot platform.
 * <p>
 * This class extends {@link AbstractSchematicMetadata} to provide platform-specific metadata handling.
 * Typically used to store and retrieve schematic-related information in Spigot-based environments.
 * </p>
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicMetadata extends AbstractSchematicMetadata {

    /**
     * Constructs a new {@code SpigotSchematicMetadata} instance with the provided metadata map.
     *
     * @param metadata a map containing schematic metadata values
     */
    public SpigotSchematicMetadata(Map<String, Object> metadata) {
        super(metadata);
    }

    /**
     * Creates a builder initialized with the current metadata.
     *
     * @return a {@link SpigotSchematicMetadataBuilder} for further modification
     */
    @Override
    public @NotNull SpigotSchematicMetadataBuilder toBuilder() {
        return new SpigotSchematicMetadataBuilder(metadata);
    }

}
