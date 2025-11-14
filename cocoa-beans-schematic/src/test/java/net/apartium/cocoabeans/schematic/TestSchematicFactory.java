package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.Map;

public class TestSchematicFactory implements SchematicFactory<TestSchematic> {

    @Override
    public SchematicBuilder<TestSchematic> createSchematic() {
        return new TestSchematic().toBuilder();
    }

    @Override
    public TestSchematic createSchematic(Instant created, MinecraftPlatform platform, SchematicMetadata metadata, BlockIterator blocks, AreaSize size, AxisOrder axisOrder, Position offset) {
        return new TestSchematic(platform, created, metadata, offset, size, axisOrder, blocks);
    }

    @Override
    public SchematicMetadata createMetadata(Map<String, Object> metadata) {
        return new TestMetaData(metadata);
    }

}
