package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadata;
import net.apartium.cocoabeans.schematic.SchematicMetadata;
import net.apartium.cocoabeans.schematic.SchematicMetadataBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicMetadata extends AbstractSchematicMetadata {

    public SpigotSchematicMetadata(Map<String, Object> metadata) {
        super(metadata);
    }

    @Override
    public <T extends SchematicMetadata> SchematicMetadataBuilder<T> builder() {
        return null;
    }

}
