package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadataBuilder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicMetadataBuilder extends AbstractSchematicMetadataBuilder<SpigotSchematicMetadata> {

    @Override
    public SpigotSchematicMetadata build() {
        return new SpigotSchematicMetadata(this.metadata);
    }

}
