package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Represents metadata of a schematic
 * @see SchematicMetadataBuilder
 * @see Schematic#metadata()
 */
@ApiStatus.AvailableSince("0.0.46")
public interface SchematicMetadata {

    static SchematicMetadataBuilder builder() {
        return new SchematicMetadataBuilderImpl(Collections.emptyMap());
    }

    static SchematicMetadata of() {
        return SchematicMetadataImpl.EMPTY;
    }

    static SchematicMetadata of(Map<String, Object> metadata) {
        return new SchematicMetadataImpl(metadata);
    }

    /**
     * Get All Schematic metadata keys
     * @return metadata keys
     */
    @NonNull Set<String> keys();

    /**
     * Get schematic metadata by key
     * @param key string key
     * @return metadata value, null if not present
     * @param <T> metadata type
     */
    <T> @Nullable T get(@NonNull String key);

    /**
     * Get schematic metadata by key
     * @param key string key
     * @param defaultValue default in case value is not present
     * @return metadata value, default if not present
     * @param <T> metadata type
     */
    <T> @Nullable T get(@NonNull String key, @NonNull T defaultValue);

    /**
     * Gets schematic author
     * @return author
     */
    @Nullable String author();

    /**
     * Gets schematic title
     * @return title
     */
    @Nullable String title();

    /**
     * Creates a new builder with the same values as this instance
     * @return a new builder instance
     */
    @NonNull SchematicMetadataBuilder toBuilder();

}
