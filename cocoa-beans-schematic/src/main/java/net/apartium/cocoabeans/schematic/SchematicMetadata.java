package net.apartium.cocoabeans.schematic;

public interface SchematicMetadata {

    <T> T get(String key);
    <T> T get(String key, T defaultValue);

    String author();
    String title();

    <T extends SchematicMetadata> SchematicMetadataBuilder<T> builder();
}
