package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadata;
import net.apartium.cocoabeans.schematic.SchematicMetadata;
import net.apartium.cocoabeans.schematic.SchematicMetadataBuilder;

import java.util.Map;

public class SpigotSchematicMetadata extends AbstractSchematicMetadata {

    public SpigotSchematicMetadata(Map<String, Object> metadata) {
        super(metadata);
    }

    @Override
    public <T extends SchematicMetadata> SchematicMetadataBuilder<T> builder() {
        return null;
    }

}
