package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockChunk;
import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.UUID;

public class TestSchematicFactory implements SchematicFactory<AbstractSchematic> {

    @Override
    public AbstractSchematic createSchematic(UUID id, Instant created, MinecraftPlatform platform, String author, String title, BlockIterator blocks, Dimensions size, AxisOrder axisOrder, Position offset) {
        AbstractSchematic schematic = new AbstractSchematic() {
            @Override
            public SchematicBuilder<?> toBuilder() {
                return null;
            }
        };

        schematic.id = id;
        schematic.created = created;
        schematic.author = author;
        schematic.title = title;

        schematic.platform = platform;

        schematic.axes = axisOrder;
        schematic.size = size;
        schematic.offset = offset;

        schematic.blockChunk = new BlockChunk(schematic.axes, 1, Position.ZERO, Position.ZERO);

        while (blocks.hasNext()) {
            BlockPlacement placement = blocks.next();
            Position position = placement.position();
            BlockData data = placement.block();

            schematic.rescaleChunkIfNeeded(position);
            schematic.blockChunk.setBlock(new BlockPlacement(position, data));
        }

        schematic.size = size;

        return schematic;
    }

}
