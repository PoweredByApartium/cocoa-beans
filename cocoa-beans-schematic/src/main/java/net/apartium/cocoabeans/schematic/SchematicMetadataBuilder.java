package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface SchematicMetadataBuilder<M extends SchematicMetadata> {

    SchematicMetadataBuilder<M> author(String author);
    SchematicMetadataBuilder<M> title(String title);

    <T> SchematicMetadataBuilder<M> set(String key, T value);

    M build();
}
