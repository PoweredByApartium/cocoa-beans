package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.apartium.cocoabeans.schematic.SchematicMetadataImpl.AUTHOR_KEY;
import static net.apartium.cocoabeans.schematic.SchematicMetadataImpl.TITLE_KEY;

/**
 * @hidden
 */
@ApiStatus.Internal
/* package-private */ class SchematicMetadataBuilderImpl implements SchematicMetadataBuilder {

    protected Map<String, Object> metadata;

    protected SchematicMetadataBuilderImpl(Map<String, Object> metadata) {
        this.metadata = new HashMap<>(metadata);
    }

    @Override
    public @NotNull SchematicMetadataBuilder author(@NotNull String author) {
        metadata.put(AUTHOR_KEY, author);
        return this;
    }

    @Override
    public @NotNull SchematicMetadataBuilder title(@NotNull String title) {
        metadata.put(TITLE_KEY, title);
        return this;
    }

    @Override
    public <T> @NotNull SchematicMetadataBuilder set(@NotNull String key, T value) {
        metadata.put(key, value);
        return this;
    }

    @Override
    public @NotNull SchematicMetadataBuilder remove(@NotNull String key) {
        metadata.remove(key);
        return this;
    }

    @Override
    public SchematicMetadata build() {
        return new SchematicMetadataImpl(metadata);
    }
}
