package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.UUID;

public class TestSchematicFactory implements SchematicFactory<TestSchematic> {

    @Override
    public TestSchematic createSchematic(UUID id, Instant created, MinecraftPlatform platform, String author, String title, BlockIterator blocks, Dimensions size, AxisOrder axisOrder, Position offset) {
        return new TestSchematic(id, platform, created, author, title, offset, size, axisOrder, blocks);
    }

}
