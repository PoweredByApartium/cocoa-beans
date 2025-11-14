package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.function.Function;

public class TestSchematic extends AbstractSchematic {

    public TestSchematic() {
        super();
    }

    public TestSchematic(MinecraftPlatform platform, Instant created, SchematicMetadata metadata, Position offset, AreaSize size, AxisOrder axes, BlockIterator iterator) {
        super(platform, created, metadata, offset, size, axes, iterator);
    }

    @Override
    public SchematicBuilder<TestSchematic> toBuilder() {
        return builder(this);
    }

    private static SchematicBuilder<TestSchematic> builder(TestSchematic schematic) {
        return new AbstractSchematicBuilder<>(schematic) {
            @Override
            public SchematicBuilder<TestSchematic> metadata(Function<SchematicMetadataBuilder<?>, SchematicMetadata> block) {
                return null;
            }

            @Override
            public TestSchematic build() {
                return new TestSchematic(platform, created, metadata, offset, size, axes, new BlockChunkIterator(blockChunk));
            }
        };
    }

}
