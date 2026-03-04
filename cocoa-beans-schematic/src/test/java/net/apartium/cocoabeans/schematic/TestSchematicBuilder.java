package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;

import java.util.function.Function;

public class TestSchematicBuilder extends AbstractSchematicBuilder<TestSchematicBuilder> {

    public TestSchematicBuilder(TestSchematic schematic) {
        super(schematic);
    }

    @Override
    protected TestSchematicBuilder self() {
        return this;
    }

    @Override
    public SchematicBuilder metadata(Function<SchematicMetadataBuilder, SchematicMetadata> block) {
        throw new IllegalStateException("Cannot modify metadata of a test schematic");
    }

    @Override
    public TestSchematic build() {
        return new TestSchematic(platform, created, metadata == null ? SchematicMetadata.of() : metadata, offset, size, axes, new BlockChunkIterator(blockChunk));
    }
}
