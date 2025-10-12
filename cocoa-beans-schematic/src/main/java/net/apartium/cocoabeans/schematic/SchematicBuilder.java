package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.axis.Axis;
import net.apartium.cocoabeans.schematic.axis.AxisOrder;
import net.apartium.cocoabeans.space.Position;

import java.time.Instant;
import java.util.UUID;

public interface SchematicBuilder {

    SchematicBuilder id(UUID id);

    SchematicBuilder created(Instant created);

    SchematicBuilder author(String author);
    SchematicBuilder title(String title);

    SchematicBuilder size(Dimensions size);

    SchematicBuilder rotate(AxisOrder axisOrder);
    SchematicBuilder flip(Axis axis);

    SchematicBuilder translate(Position offset);
    SchematicBuilder translate(AxisOrder axisOrder);

    SchematicBuilder setBlock(int x, int y, int z, BlockData data);
    SchematicBuilder removeBlock(int x, int y, int z);

    Schematic build();

}
