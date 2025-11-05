package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;

import java.time.Instant;
import java.util.UUID;

public class TestSchematic extends AbstractSchematic {

    public TestSchematic() {
        super();
    }

    public TestSchematic(UUID id, MinecraftPlatform platform, Instant created, String author, String title, Position offset, Dimensions size, AxisOrder axes, BlockIterator iterator) {
        super(id, platform, created, author, title, offset, size, axes, iterator);
    }

    @Override
    public SchematicBuilder<?> toBuilder() {
        return builder(this);
    }

    private static SchematicBuilder<TestSchematic> builder(TestSchematic schematic) {
        return new AbstractSchematicBuilder<>(schematic) {
            @Override
            public TestSchematic build() {
                return new TestSchematic(id, platform, created, author, title, offset, size, axes, new BlockChunkIterator(blockChunk));
            }
        };
    }

}
