package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.schematic.axis.AxisOrder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class TestSchematicFactory implements SchematicFactory<AbstractSchematic> {

    @Override
    public AbstractSchematic createSchematic() {
        return new AbstractSchematic();
    }

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

        schematic.blocks = new BlockData[(int) axisOrder.getFirst().getAlong(size)][(int) axisOrder.getSecond().getAlong(size)][(int) axisOrder.getThird().getAlong(size)];

        for (Map.Entry<Position, BlockData> block : blocks.entrySet()) {
            Position position = block.getKey();
            BlockData data = block.getValue();

            schematic.blocks[(int) axisOrder.getFirst().getAlong(position)][(int) axisOrder.getSecond().getAlong(position)][(int) axisOrder.getThird().getAlong(position)] = data;
        }

        schematic.size = size;

        return schematic;
    }
}
