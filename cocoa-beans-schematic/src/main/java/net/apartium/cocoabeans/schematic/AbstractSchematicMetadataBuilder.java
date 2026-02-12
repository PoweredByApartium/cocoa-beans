package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.apartium.cocoabeans.schematic.AbstractSchematicMetadata.AUTHOR_KEY;
import static net.apartium.cocoabeans.schematic.AbstractSchematicMetadata.TITLE_KEY;

/**
 * @hidden
 */
@ApiStatus.Internal
public abstract class AbstractSchematicMetadataBuilder<M extends SchematicMetadata> implements SchematicMetadataBuilder<M> {

    protected Map<String, Object> metadata;

    protected AbstractSchematicMetadataBuilder() {
        this(Map.of());
    }

    protected AbstractSchematicMetadataBuilder(Map<String, Object> metadata) {
        this.metadata = new HashMap<>(metadata);
    }

    @Override
    public @NotNull SchematicMetadataBuilder<@NotNull M> author(@NotNull String author) {
        metadata.put(AUTHOR_KEY, author);
        return this;
    }

    @Override
    public @NotNull SchematicMetadataBuilder<@NotNull M> title(@NotNull String title) {
        metadata.put(TITLE_KEY, title);
        return this;
    }

    @Override
    public <T> @NotNull SchematicMetadataBuilder<@NotNull M> set(@NotNull String key, T value) {
        metadata.put(key, value);
        return this;
    }

    @Override
    public @NotNull SchematicMetadataBuilder<@NotNull M> remove(@NotNull String key) {
        metadata.remove(key);
        return this;
    }

}
