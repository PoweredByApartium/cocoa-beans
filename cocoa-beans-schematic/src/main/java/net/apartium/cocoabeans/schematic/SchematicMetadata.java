package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface SchematicMetadata {

    <T> T get(String key);
    <T> T get(String key, T defaultValue);

    String author();
    String title();

    <T extends SchematicMetadata> SchematicMetadataBuilder<T> builder();
}
