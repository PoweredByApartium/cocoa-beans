package net.apartium.cocoabeans.schematic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchematicMetadataBuilderImplTest {

    @Test
    void empty() {
        SchematicMetadata metadata = SchematicMetadata.builder().build();
        assertNull(metadata.author());
        assertNull(metadata.title());
        assertEquals(0, metadata.keys().size());
    }

    @Test
    void author() {
        SchematicMetadata metadata = SchematicMetadata.builder()
                .author("Test Author")
                .build();

        assertEquals("Test Author", metadata.author());
        assertNull(metadata.title());
        assertEquals(1, metadata.keys().size());
    }

    @Test
    void title() {
        SchematicMetadata metadata = SchematicMetadata.builder()
                .title("Test Title")
                .build();

        assertNull(metadata.author());
        assertEquals("Test Title", metadata.title());
        assertEquals(1, metadata.keys().size());
    }

    @Test
    void authorAndTitle() {
        SchematicMetadata metadata = SchematicMetadata.builder()
                .author("Test Author")
                .title("Test Title")
                .build();

        assertEquals("Test Author", metadata.author());
        assertEquals("Test Title", metadata.title());
        assertEquals(2, metadata.keys().size());
    }

    @Test
    void customKey() {
        SchematicMetadata metadata = SchematicMetadata.builder()
                .set("key", "Test value")
                .build();

        assertEquals("Test value", metadata.get("key"));
        assertEquals(1, metadata.keys().size());
    }
}
