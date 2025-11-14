package net.apartium.cocoabeans.schematic;

import java.util.Map;

public class TestMetaData extends AbstractSchematicMetadata {

    public TestMetaData(Map<String, Object> metadata) {
        super(metadata);
    }

    @Override
    public SchematicMetadataBuilder<TestMetaData> toBuilder() {
        return new AbstractSchematicMetadataBuilder<>() {
            @Override
            public TestMetaData build() {
                return new TestMetaData(metadata);
            }
        };
    }

}
