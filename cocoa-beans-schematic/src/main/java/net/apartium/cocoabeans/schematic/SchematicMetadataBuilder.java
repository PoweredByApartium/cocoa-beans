package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * A schematic metadata builder
 * @see SchematicMetadata#builder()
 * @see SchematicBuilder
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public interface SchematicMetadataBuilder {

    /**
     * Set schematic author
     * @param author schematic author
     * @return this builder instance
     */
    SchematicMetadataBuilder author(String author);

    /**
     * Set schematic title
     * @param title schematic title
     * @return this builder instance
     */
    SchematicMetadataBuilder title(String title);

    /**
     * Set generic metadata
     * @param key key
     * @param value value
     * @return this builder instance
     * @param <T> metadata type
     */
    <T> SchematicMetadataBuilder set(String key, T value);

    /**
     * Remove generic metadata
     * @param key key
     * @return this builder instance
     */
    SchematicMetadataBuilder remove(String key);

    /**
     * Build a new instance of schematic metadata with this builder's data
     * @return a new schematic metadata instance
     */
    SchematicMetadata build();
}
