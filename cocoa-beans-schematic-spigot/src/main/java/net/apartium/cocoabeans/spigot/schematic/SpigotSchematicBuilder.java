package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematicBuilder extends AbstractSchematicBuilder<SpigotSchematic> {

    public SpigotSchematicBuilder() {}

    public SpigotSchematicBuilder(Schematic schematic) {
        super(schematic);
    }

    @Override
    public SchematicBuilder<SpigotSchematic> metadata(Function<SchematicMetadataBuilder<?>, SchematicMetadata> block) {
        return metadata(block.apply(new SpigotSchematicMetadataBuilder()));
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
