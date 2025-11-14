package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadataBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicMetadataBuilder extends AbstractSchematicMetadataBuilder<SpigotSchematicMetadata> {

    public SpigotSchematicMetadataBuilder() {
        super(new HashMap<>());
    }

    public SpigotSchematicMetadataBuilder(Map<String, Object> metadata) {
        super(metadata);
    }

    @Override
    public SpigotSchematicMetadata build() {
        return new SpigotSchematicMetadata(this.metadata);
    }

}
