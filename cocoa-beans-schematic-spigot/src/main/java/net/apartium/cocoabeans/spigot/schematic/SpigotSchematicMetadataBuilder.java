package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicMetadataBuilder;

public class SpigotSchematicMetadataBuilder extends AbstractSchematicMetadataBuilder<SpigotSchematicMetadata> {

    @Override
    public SpigotSchematicMetadata build() {
        return new SpigotSchematicMetadata(this.metadata);
    }

}
