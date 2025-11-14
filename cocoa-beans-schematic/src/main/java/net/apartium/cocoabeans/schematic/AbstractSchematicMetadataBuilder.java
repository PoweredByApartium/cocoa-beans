package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

import static net.apartium.cocoabeans.schematic.AbstractSchematicMetadata.AUTHOR_KEY;
import static net.apartium.cocoabeans.schematic.AbstractSchematicMetadata.TITLE_KEY;

/**
 * @hidden
 * @param <M>
 */
@ApiStatus.Internal
public abstract class AbstractSchematicMetadataBuilder<M extends SchematicMetadata> implements SchematicMetadataBuilder<M> {

    protected Map<String, Object> metadata;

    public AbstractSchematicMetadataBuilder() {
        this(Map.of());
    }

    public AbstractSchematicMetadataBuilder(Map<String, Object> metadata) {
        this.metadata = new HashMap<>(metadata);
    }

    @Override
    public SchematicMetadataBuilder<M> author(String author) {
        metadata.put(AUTHOR_KEY, author);
        return this;
    }

    @Override
    public SchematicMetadataBuilder<M> title(String title) {
        metadata.put(TITLE_KEY, title);
        return this;
    }

    @Override
    public <T> SchematicMetadataBuilder<M> set(String key, T value) {
        metadata.put(key, value);
        return this;
    }

    @Override
    public SchematicMetadataBuilder<M> remove(String key) {
        metadata.remove(key);
        return this;
    }

}
