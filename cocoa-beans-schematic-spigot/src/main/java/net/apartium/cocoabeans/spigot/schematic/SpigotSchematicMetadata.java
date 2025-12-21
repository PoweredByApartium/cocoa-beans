package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// todo remove
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicMetadata extends AbstractSchematicMetadata {

    public SpigotSchematicMetadata(Map<String, Object> metadata) {
        super(metadata);
    }

    @Override
    public @NotNull SpigotSchematicMetadataBuilder toBuilder() {
        return new SpigotSchematicMetadataBuilder(metadata);
    }

}
