package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @hidden
 */
@ApiStatus.Internal
/* package-private */ class SchematicMetadataImpl implements SchematicMetadata {

    static final SchematicMetadata EMPTY = new SchematicMetadataImpl(Map.of());

    public static final String AUTHOR_KEY = "author";
    public static final String TITLE_KEY = "title";

    protected final Map<String, Object> metadata;

    public SchematicMetadataImpl(Map<String, Object> metadata) {
        this.metadata = Map.copyOf(metadata);
    }

    @Override
    public @NonNull @NotNull Set<@NotNull String> keys() {
        return metadata.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T> T get(@NotNull String key) {
        return (T) metadata.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@NotNull String key, @NotNull T defaultValue) {
        return (T) metadata.getOrDefault(key, defaultValue);
    }

    @Override
    public String author() {
        return (String) metadata.get(AUTHOR_KEY);
    }

    @Override
    public String title() {
        return (String) metadata.get(TITLE_KEY);
    }

    @Override
    public @NonNull SchematicMetadataBuilder toBuilder() {
        return new SchematicMetadataBuilderImpl(new HashMap<>(metadata));
    }
}
