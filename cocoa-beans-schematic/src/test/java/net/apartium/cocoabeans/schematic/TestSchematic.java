package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.format.BodyExtension;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@NullMarked
public class TestSchematic extends AbstractSchematic<TestSchematic> {

    private final Map<Long, BodyExtension<?>> bodyExtensionMap;

    public TestSchematic(MinecraftPlatform platform, Instant created, SchematicMetadata metadata, Position offset, AreaSize size, AxisOrder axes, BlockIterator iterator) {
        this(platform, created, metadata, offset, size, axes, iterator, Map.of());
    }

    public TestSchematic(MinecraftPlatform platform, Instant created, SchematicMetadata metadata, Position offset, AreaSize size, AxisOrder axes, BlockIterator iterator, Map<Long, BodyExtension<?>> bodyExtensions) {
        super(platform, created, metadata, offset, size, axes, iterator);
        this.bodyExtensionMap = Map.copyOf(bodyExtensions);
    }

    @Override
    public Set<BodyExtension<?>> bodyExtensions() {
        return new HashSet<>(bodyExtensionMap.values());
    }

    @Override
    public SchematicBuilder<TestSchematic> toBuilder() {
        return builder(this);
    }

    private static SchematicBuilder<TestSchematic> builder(TestSchematic schematic) {
        return new AbstractSchematicBuilder<>(schematic) {
            @Override
            public SchematicBuilder<TestSchematic> metadata(Function<SchematicMetadataBuilder, SchematicMetadata> block) {
                throw new IllegalStateException("Cannot modify metadata of a test schematic");
            }

            @Override
            public TestSchematic build() {
                return new TestSchematic(platform, created, metadata == null ? SchematicMetadata.of() : metadata, offset, size, axes, new BlockChunkIterator(blockChunk));
            }
        };
    }

}
