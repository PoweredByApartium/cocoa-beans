package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.UUID;

public interface SchematicBuilder {

    SchematicBuilder id(UUID id);

    SchematicBuilder platform(MinecraftPlatform platform);
    SchematicBuilder created(Instant created);

    SchematicBuilder author(String author);
    SchematicBuilder title(String title);

    SchematicBuilder size(Dimensions size);

    SchematicBuilder rotate(int degrees);
    SchematicBuilder flip(Axis axis);

    SchematicBuilder translate(Position offset);
    SchematicBuilder translate(AxisOrder axisOrder);

    SchematicBuilder setBlock(int x, int y, int z, BlockData data);
    SchematicBuilder removeBlock(int x, int y, int z);

    Schematic build();

}
