package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * @hidden
 */
@ApiStatus.Internal
public abstract class AbstractSchematicMetadata implements SchematicMetadata {

    public static final String AUTHOR_KEY = "author";
    public static final String TITLE_KEY = "title";

    protected final Map<String, Object> metadata;

    public AbstractSchematicMetadata(Map<String, Object> metadata) {
        this.metadata = Map.copyOf(metadata);
    }

    @Override
    public @NonNull Set<String> keys() {
        return metadata.keySet();
    }

    @Override
    public <T> T get(String key) {
        return (T) metadata.get(key);
    }

    @Override
    public <T> T get(String key, T defaultValue) {
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

}
