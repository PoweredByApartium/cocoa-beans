package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.format.StringBlockPropFormat;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class StringBlockPropFormatTest {

    @Test
    void serialize() {
        StringBlockPropFormat format = new StringBlockPropFormat();

        String[] cases = {
                "",
                "a",
                "hello",
                "שלום",
                "🔥",
                "abc😀def",
                "line\nbreak"
        };

        for (String s : cases) {
            byte[] encoded = format.encode(new StringBlockProp(s));

            byte[] utf8 = s.getBytes(StandardCharsets.UTF_8);
            assertEquals(utf8.length + 1, encoded.length);
            assertEquals((byte) utf8.length, encoded[0]);

            byte[] payload = new byte[utf8.length];
            System.arraycopy(encoded, 1, payload, 0, utf8.length);
            assertArrayEquals(utf8, payload);
        }
    }

    @Test
    void deserialize() {
        StringBlockPropFormat format = new StringBlockPropFormat();

        String[] cases = {
                "",
                "a",
                "hello",
                "שלום",
                "🔥",
                "abc😀def"
        };

        for (String s : cases) {
            byte[] utf8 = s.getBytes(StandardCharsets.UTF_8);

            byte[] data = new byte[utf8.length + 1];
            data[0] = (byte) utf8.length;
            System.arraycopy(utf8, 0, data, 1, utf8.length);

            assertEquals(s, format.decode(data).value());
        }
    }

    @Test
    void roundtripMany() {
        StringBlockPropFormat format = new StringBlockPropFormat();

        for (int len = 0; len <= 255; len++) {
            String s = "a".repeat(len);

            byte[] encoded = format.encode(new StringBlockProp(s));
            String decoded = format.decode(encoded).value();

            assertEquals(s, decoded);
        }
    }

    @Test
    void failedSerializeWrongType() {
        StringBlockPropFormat format = new StringBlockPropFormat();

        assertThrows(IllegalArgumentException.class, () -> format.encode(new IntBlockProp(1)));
        assertThrows(IllegalArgumentException.class, () -> format.encode(new ByteBlockProp((byte) 1)));
    }

    @Test
    void failedSerializeNullValue() {
        StringBlockPropFormat format = new StringBlockPropFormat();

        BlockProp<?> nullProp = () -> null;

        assertThrows(NullPointerException.class, () -> format.encode(nullProp));
    }

    @Test
    void failedSerializeTooLongUtf8() {
        StringBlockPropFormat format = new StringBlockPropFormat();

        String tooLong = "a".repeat(StringBlockPropFormat.MAX_LENGTH + 1);
        assertEquals(256, tooLong.getBytes(StandardCharsets.UTF_8).length);

        assertThrows(IllegalArgumentException.class, () -> format.encode(new StringBlockProp(tooLong)));
    }

    @Test
    void failedDeserializeEmptyOrTruncated() {
        StringBlockPropFormat format = new StringBlockPropFormat();

        assertThrows(UncheckedIOException.class, () -> format.decode(new byte[0]));

        assertThrows(UncheckedIOException.class, () -> format.decode(new byte[] {5, 'a', 'b'}));

        assertEquals("", format.decode(new byte[] {0}).value());
    }
}