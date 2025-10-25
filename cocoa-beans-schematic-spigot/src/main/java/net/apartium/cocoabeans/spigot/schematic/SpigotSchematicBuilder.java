package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematic;
import net.apartium.cocoabeans.schematic.AbstractSchematicBuilder;
import net.apartium.cocoabeans.schematic.SchematicBuilder;

public class SpigotSchematicBuilder extends AbstractSchematicBuilder<SpigotSchematic> {

    public SpigotSchematicBuilder(AbstractSchematic schematic) {
        super(schematic);
    }

    @Override
    public SchematicBuilder toBuilder() {
        return null;
    }

    @Override
    public SpigotSchematic build() {
        return new SpigotSchematic(this);
    }
}
