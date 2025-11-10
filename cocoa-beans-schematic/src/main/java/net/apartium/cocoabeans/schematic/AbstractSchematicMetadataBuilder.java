package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

import static net.apartium.cocoabeans.schematic.AbstractSchematicMetadata.AUTHOR_KEY;
import static net.apartium.cocoabeans.schematic.AbstractSchematicMetadata.TITLE_KEY;

@ApiStatus.AvailableSince("0.0.46")
public abstract class AbstractSchematicMetadataBuilder<M extends SchematicMetadata> implements SchematicMetadataBuilder<M> {

    protected Map<String, Object> metadata = new HashMap<>();

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

}
