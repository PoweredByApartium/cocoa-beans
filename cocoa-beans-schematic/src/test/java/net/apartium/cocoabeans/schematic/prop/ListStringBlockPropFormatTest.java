package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.format.ListStringBlockPropFormat;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListStringBlockPropFormatTest {

    private static final class ListStringProp implements BlockProp<List<String>> {
        private final List<String> value;
        private ListStringProp(List<String> value) { this.value = value; }
        @Override public List<String> value() { return value; }
    }

    @Test
    void roundtrip() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        List<List<String>> cases = List.of(
                List.of(),
                List.of("a"),
                List.of("hello", "world"),
                List.of("", "x", ""),
                List.of("שלום", "מה קורה", "🔥", "😀 emoji", "line\nbreak")
        );

        for (List<String> input : cases) {
            byte[] encoded = format.encode(new ListStringProp(input));
            BlockProp<List<String>> decoded = format.decode(encoded);

            assertEquals(input, decoded.value());
        }
    }

    @Test
    void encodeProducesExpectedBytesSimpleAscii() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        List<String> input = List.of("ab", "c");
        byte[] encoded = format.encode(new ListStringProp(input));

        assertArrayEquals(new byte[] {2, 'a', 'b', 1, 'c'}, encoded);
    }

    @Test
    void decodeEmpty() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        BlockProp<List<String>> decoded = format.decode(new byte[0]);
        assertEquals(List.of(), decoded.value());
    }

    @Test
    void failedEncodeNullValue() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        BlockProp<?> nullProp = () -> null;
        assertThrows(NullPointerException.class, () -> format.encode(nullProp));
    }

    @Test
    void failedEncodeWrongType() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        assertThrows(IllegalArgumentException.class, () -> format.encode(new IntBlockProp(1)));
    }

    @Test
    void failedEncodeListContainsNonString() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        List<Object> mixed = new ArrayList<>();
        mixed.add("ok");
        mixed.add(123);

        BlockProp<?> bad = () -> mixed;
        assertThrows(IllegalArgumentException.class, () -> format.encode(bad));
    }

    @Test
    void failedEncodeTooLongUtf8() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        String tooLong = "a".repeat(ListStringBlockPropFormat.MAX_LENGTH + 1);
        assertEquals(256, tooLong.getBytes(StandardCharsets.UTF_8).length);

        assertThrows(IllegalArgumentException.class, () -> format.encode(new ListStringProp(List.of(tooLong))));
    }

    @Test
    void failedDecodeTruncatedPayload() {
        ListStringBlockPropFormat format = ListStringBlockPropFormat.INSTANCE;

        byte[] bad = new byte[] {5, 'a', 'b'};

        assertThrows(UncheckedIOException.class, () -> format.decode(bad));
    }
}