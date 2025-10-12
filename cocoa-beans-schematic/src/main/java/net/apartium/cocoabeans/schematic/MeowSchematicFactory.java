package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.Position;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MeowSchematicFactory implements SchematicFactory<AbstractSchematic> {

    @Override
    public AbstractSchematic createSchematic(UUID id, Instant created, String author, String title, Map<Position, BlockData> blocks, Dimensions size, AxisOrder axisOrder, Position offset) {
        AbstractSchematic schematic = new AbstractSchematic();

        schematic.id = id;
        schematic.created = created;
        schematic.author = author;
        schematic.title = title;

        schematic.axes = axisOrder;
        schematic.size = size;
        schematic.offset = offset;

        schematic.blockChunk = new BlockChunk(schematic.axes, Math.pow(4, 6), Position.ZERO, Position.ZERO);

        for (Map.Entry<Position, BlockData> block : blocks.entrySet()) {
            Position position = block.getKey();
            BlockData data = block.getValue();

            schematic.blockChunk.setBlock(position, data);
        }

        schematic.size = size;

        return schematic;
    }

}
