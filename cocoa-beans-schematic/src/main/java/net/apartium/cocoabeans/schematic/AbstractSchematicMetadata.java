package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.AvailableSince("0.0.46")
public abstract class AbstractSchematicMetadata implements SchematicMetadata{

    public static final String AUTHOR_KEY = "author";
    public static final String TITLE_KEY = "title";

    private final Map<String, Object> metadata;

    public AbstractSchematicMetadata(Map<String, Object> metadata) {
        this.metadata = Map.copyOf(metadata);
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
