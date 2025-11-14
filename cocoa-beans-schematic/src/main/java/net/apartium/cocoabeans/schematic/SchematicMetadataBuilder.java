package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * A schematic metadata builder
 * @see SchematicMetadata
 * @see SchematicBuilder
 * @param <M>
 * todo kfir remove inheritance
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public interface SchematicMetadataBuilder<M extends SchematicMetadata> {

    /**
     * Set schematic author
     * @param author schematic author
     * @return this builder instance
     */
    SchematicMetadataBuilder<M> author(String author);

    /**
     * Set schematic title
     * @param title schematic title
     * @return this builder instance
     */
    SchematicMetadataBuilder<M> title(String title);

    /**
     * Set generic metadata
     * @param key key
     * @param value value
     * @return this builder instance
     * @param <T> metadata type
     */
    <T> SchematicMetadataBuilder<M> set(String key, T value);

    /**
     * Remove generic metadata
     * @param key key
     * @return this builder instance
     */
    SchematicMetadataBuilder<M> remove(String key);

    /**
     * Build a new instance of schematic metadata with this builder's data
     * @return a new schematic metadata instance
     */
    M build();
}
