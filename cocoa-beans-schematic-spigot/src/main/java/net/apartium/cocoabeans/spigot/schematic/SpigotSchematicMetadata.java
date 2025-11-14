package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadata;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

// todo remove
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicMetadata extends AbstractSchematicMetadata {

    public SpigotSchematicMetadata(Map<String, Object> metadata) {
        super(metadata);
    }

    @Override
    public SpigotSchematicMetadataBuilder toBuilder() {
        return new SpigotSchematicMetadataBuilder(metadata);
    }

}
