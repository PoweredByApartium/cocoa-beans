package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.AbstractSchematicBuilder;
import net.apartium.cocoabeans.schematic.Schematic;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;

public class SpigotSchematicBuilder extends AbstractSchematicBuilder<SpigotSchematic> {

    public SpigotSchematicBuilder() {}

    public SpigotSchematicBuilder(Schematic schematic) {
        super(schematic);
    }

    @Override
    public SpigotSchematic build() {
        return new SpigotSchematic(
                platform,
                created,
                metadata,
                offset,
                size,
                axes,
                new BlockChunkIterator(blockChunk)
        );
    }
}
