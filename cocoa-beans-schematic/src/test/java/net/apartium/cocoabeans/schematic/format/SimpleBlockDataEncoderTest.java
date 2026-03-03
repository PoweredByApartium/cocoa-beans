package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import net.apartium.cocoabeans.schematic.prop.StringBlockProp;
import net.apartium.cocoabeans.schematic.prop.format.StringBlockPropFormat;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static net.apartium.cocoabeans.utils.BufferUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBlockDataEncoderTest {

    private static SeekableInputStream toStream(byte[] data) {
        return new SeekableInputStream(ByteArraySeekableChannel.of(data));
    }

    @Test
    void roundtripNoProps() {
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(Map.of());
        BlockData block = new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());

        byte[] bytes = encoder.write(block);
        BlockData decoded = encoder.read(toStream(bytes));

        assertEquals("minecraft", decoded.type().namespace());
        assertEquals("stone", decoded.type().key());
        assertTrue(decoded.props().isEmpty());
    }

    @Test
    void roundtripSingleProp() {
        TestBlockPropFormat format = new TestBlockPropFormat();
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(Map.of("count", format));

        Map<String, BlockProp<?>> props = new LinkedHashMap<>();
        props.put("count", new IntBlockProp(42));
        BlockData block = new GenericBlockData(new NamespacedKey("minecraft", "stone"), props);

        byte[] bytes = encoder.write(block);
        BlockData decoded = encoder.read(toStream(bytes));

        assertEquals("minecraft", decoded.type().namespace());
        assertEquals("stone", decoded.type().key());
        assertEquals(42, decoded.props().get("count").value());
        assertEquals(1, format.getEncodeCallCount());
        assertEquals(1, format.getDecodeCallCount());
    }

    @Test
    void roundtripMultipleProps() {
        TestBlockPropFormat testFormat = new TestBlockPropFormat();
        StringBlockPropFormat stringFormat = new StringBlockPropFormat();

        Map<String, net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat<?>> formatMap = new LinkedHashMap<>();
        formatMap.put("level", testFormat);
        formatMap.put("name", stringFormat);
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(formatMap);

        Map<String, BlockProp<?>> props = new LinkedHashMap<>();
        props.put("level", new IntBlockProp(7));
        props.put("name", new StringBlockProp("oak"));
        BlockData block = new GenericBlockData(new NamespacedKey("minecraft", "oak_log"), props);

        byte[] bytes = encoder.write(block);
        BlockData decoded = encoder.read(toStream(bytes));

        assertEquals("minecraft", decoded.type().namespace());
        assertEquals("oak_log", decoded.type().key());
        assertEquals(7, decoded.props().get("level").value());
        assertEquals("oak", decoded.props().get("name").value());
        assertEquals(1, testFormat.getEncodeCallCount());
        assertEquals(1, testFormat.getDecodeCallCount());
    }

    @Test
    void roundtripVariousNamespacedKeys() {
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(Map.of());

        NamespacedKey[] keys = {
                new NamespacedKey("minecraft", "air"),
                new NamespacedKey("minecraft", "grass_block"),
                new NamespacedKey("custom_mod", "special_block"),
                new NamespacedKey("a", "b"),
                new NamespacedKey("", ""),
        };

        for (NamespacedKey key : keys) {
            BlockData block = new GenericBlockData(key, Map.of());
            byte[] bytes = encoder.write(block);
            BlockData decoded = encoder.read(toStream(bytes));
            assertEquals(key.namespace(), decoded.type().namespace());
            assertEquals(key.key(), decoded.type().key());
        }
    }

    @Test
    void roundtripMany() {
        TestBlockPropFormat format = new TestBlockPropFormat();
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(Map.of("value", format));

        for (int i = 0; i < 1_000; i++) {
            int val = (i * 1103515245) ^ (i >>> 3) ^ 0xA5A5A5A5;

            Map<String, BlockProp<?>> props = new LinkedHashMap<>();
            props.put("value", new IntBlockProp(val));
            BlockData block = new GenericBlockData(new NamespacedKey("minecraft", "stone"), props);

            byte[] bytes = encoder.write(block);
            BlockData decoded = encoder.read(toStream(bytes));

            assertEquals(val, decoded.props().get("value").value());
        }
    }

    @Test
    void writeThrowsOnUnknownProp() {
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(Map.of());

        Map<String, BlockProp<?>> props = new LinkedHashMap<>();
        props.put("count", new IntBlockProp(5));
        BlockData block = new GenericBlockData(new NamespacedKey("minecraft", "stone"), props);

        assertThrows(IllegalArgumentException.class, () -> encoder.write(block));
    }

    @Test
    void readThrowsOnUnknownPropType() {
        TestBlockPropFormat format = new TestBlockPropFormat();
        SimpleBlockDataEncoder writeEncoder = new SimpleBlockDataEncoder(Map.of("count", format));
        SimpleBlockDataEncoder readEncoder = new SimpleBlockDataEncoder(Map.of());

        Map<String, BlockProp<?>> props = new LinkedHashMap<>();
        props.put("count", new IntBlockProp(10));
        BlockData block = new GenericBlockData(new NamespacedKey("minecraft", "stone"), props);

        byte[] bytes = writeEncoder.write(block);

        try(SeekableInputStream stream = toStream(bytes)) {
            assertThrows(NoSuchElementException.class, () -> readEncoder.read(stream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void readThrowsOnDuplicatePropType() {
        TestBlockPropFormat format = new TestBlockPropFormat();
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(Map.of("count", format));

        byte[] val = {0, 0, 0, 1};

        ByteArrayOutputStream payloadOut = new ByteArrayOutputStream();
        payloadOut.write(writeString("minecraft"), 0, writeString("minecraft").length);
        payloadOut.write(writeString("stone"), 0, writeString("stone").length);

        byte[] propKey = writeString("count");
        byte[] valLen = writeU24(val.length);

        payloadOut.write(propKey, 0, propKey.length);
        payloadOut.write(valLen, 0, valLen.length);
        payloadOut.write(val, 0, val.length);

        // duplicate prop type
        payloadOut.write(propKey, 0, propKey.length);
        payloadOut.write(valLen, 0, valLen.length);
        payloadOut.write(val, 0, val.length);

        byte[] payload = payloadOut.toByteArray();

        ByteArrayOutputStream fullOut = new ByteArrayOutputStream();
        byte[] sizeHeader = writeU32(payload.length);
        fullOut.write(sizeHeader, 0, sizeHeader.length);
        fullOut.write(payload, 0, payload.length);

        byte[] fullBytes = fullOut.toByteArray();

        try (SeekableInputStream stream = toStream(fullBytes)) {
            assertThrows(IllegalArgumentException.class, () -> encoder.read(stream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void binaryStartsWithPayloadSize() {
        TestBlockPropFormat format = new TestBlockPropFormat();
        SimpleBlockDataEncoder encoder = new SimpleBlockDataEncoder(Map.of("count", format));

        Map<String, BlockProp<?>> props = new LinkedHashMap<>();
        props.put("count", new IntBlockProp(0));
        BlockData block = new GenericBlockData(new NamespacedKey("minecraft", "stone"), props);

        byte[] bytes = encoder.write(block);

        long payloadSize = ((bytes[0] & 0xFFL) << 24)
                | ((bytes[1] & 0xFFL) << 16)
                | ((bytes[2] & 0xFFL) << 8)
                | (bytes[3] & 0xFFL);

        assertEquals(bytes.length - 4, payloadSize);
    }

    @Test
    void idConstant() {
        assertEquals(0b1, SimpleBlockDataEncoder.ID);
    }
}
