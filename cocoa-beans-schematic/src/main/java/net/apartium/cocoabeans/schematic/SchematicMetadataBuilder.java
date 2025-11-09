package net.apartium.cocoabeans.schematic;

public interface SchematicMetadataBuilder<M extends SchematicMetadata> {

    SchematicMetadataBuilder<M> author(String author);
    SchematicMetadataBuilder<M> title(String title);

    <T> SchematicMetadataBuilder<M> set(String key, T value);

    M build();
}
