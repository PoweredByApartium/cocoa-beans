package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Represents metadata of a schematic
 * @see SchematicMetadataBuilder
 * @see Schematic#metadata()
 */
@ApiStatus.AvailableSince("0.0.46")
public interface SchematicMetadata {

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
    <T> @NonNull T get(@NonNull String key, @NonNull T defaultValue);

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
     * @param <T> schematic metadata type
     */
    @NonNull <T extends SchematicMetadata> SchematicMetadataBuilder<T> toBuilder();

}
