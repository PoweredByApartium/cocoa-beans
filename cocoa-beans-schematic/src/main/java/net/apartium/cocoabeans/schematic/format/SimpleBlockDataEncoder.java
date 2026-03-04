package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import org.jetbrains.annotations.ApiStatus;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

import static net.apartium.cocoabeans.utils.BufferUtils.*;

/**
 * A straightforward {@link BlockDataEncoder} that serialises each {@link BlockData} value as a
 * length-prefixed payload containing the block's namespaced type key followed by its properties.
 *
 * <h2>Binary format</h2>
 * <pre>
 *   [4 bytes] payload length (unsigned 32-bit, big-endian)
 *   [payload]
 *     [string] namespace   – namespace portion of the block's {@link NamespacedKey}
 *     [string] key         – key portion of the block's {@link NamespacedKey}
 *     [prop…]  zero or more property entries (see below)
 * </pre>
 *
 * Each <em>property entry</em> in the payload:
 * <pre>
 *   [string]  type name    – property identifier, used to look up a {@link BlockPropFormat}
 *   [3 bytes] value length – unsigned 24-bit length of the encoded value (max ~16 MB)
 *   [bytes]   value        – raw bytes produced by {@link BlockPropFormat#encode}
 * </pre>
 *
 * <p>Strings are encoded in the format expected by {@code BufferUtils.readString} /
 * {@code BufferUtils.writeStringAsList} (length-prefixed UTF-8).</p>
 *
 * @see BlockDataEncoder
 * @see BlockPropFormat
 */
@ApiStatus.AvailableSince("0.0.46")
public class SimpleBlockDataEncoder implements BlockDataEncoder {

    /** Numeric identifier that distinguishes this encoder in the schematic file header. */
    public static final int ID = 0b1;

    /** Map from property type name to the format responsible for encoding/decoding that property. */
    private final Map<String, BlockPropFormat<?>> propFormatMap;

    /**
     * Creates a new encoder backed by the given property-format registry.
     *
     * @param formatMap map from property type name to its {@link BlockPropFormat}; must contain
     *                  an entry for every property type that will be encountered during reads and
     *                  writes
     */
    public SimpleBlockDataEncoder(Map<String, BlockPropFormat<?>> formatMap) {
        this.propFormatMap = formatMap;
    }

    /**
     * Deserialises a single {@link BlockData} value from {@code stream}.
     *
     * <p>The method reads the 4-byte payload length, consumes exactly that many bytes into an
     * in-memory buffer, then decodes the namespaced key and each property entry in order.
     * Properties are decoded using the {@link BlockPropFormat} registered for their type name.</p>
     *
     * @param stream the seekable stream positioned at the start of a block-data entry
     * @return the decoded {@link BlockData}
     * @throws NoSuchElementException   if a property type name is not present in the format map
     * @throws IllegalArgumentException if the same property type appears more than once
     * @throws UncheckedIOException     if an I/O error occurs while reading the stream
     */
    @Override
    public BlockData read(SeekableInputStream stream) {
        try {
            DataInputStream in = new DataInputStream(stream);

            int payloadSize = (int) readU32(in);
            byte[] payload = in.readNBytes(payloadSize);

            ByteArrayInputStream bin = new ByteArrayInputStream(payload);
            DataInputStream din = new DataInputStream(bin);

            NamespacedKey namespacedKey = new NamespacedKey(
                    readString(din),
                    readString(din)
            );

            Map<String, BlockProp<?>> props = new LinkedHashMap<>();
            while (din.available() > 0) {
                String type = readString(din);
                BlockPropFormat<?> propFormat = propFormatMap.get(type);

                if (propFormat == null)
                    throw new NoSuchElementException("Unknown prop type: " + type);

                if (props.containsKey(type))
                    throw new IllegalArgumentException("Duplicate prop of type: " + type);

                int valueLength = readU24(din);
                byte[] valueBytes = din.readNBytes(valueLength);

                props.put(type, propFormat.decode(valueBytes));
            }

            return new GenericBlockData(namespacedKey, props);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Serialises a {@link BlockData} value into the binary format described in the class Javadoc.
     *
     * <p>The block's namespaced type key is written first, followed by one entry per property.
     * Each property value is encoded by its corresponding {@link BlockPropFormat} and prefixed
     * with a 3-byte (unsigned 24-bit) length field. The entire payload is then prefixed with its
     * 4-byte total length.</p>
     *
     * @param blockData the block data to serialise
     * @return the serialised bytes, including the 4-byte length prefix
     * @throws IllegalArgumentException if a property's type name is not present in the format map
     */
    @Override
    public byte[] write(BlockData blockData) {
        List<Byte> bytes = new LinkedList<>();

        // Type
        bytes.addAll(writeStringAsList(blockData.type().namespace()));
        bytes.addAll(writeStringAsList(blockData.type().key()));

        // Prop
        for (Map.Entry<String, BlockProp<?>> entry : blockData.props().entrySet()) {
            BlockPropFormat<?> propFormat = propFormatMap.get(entry.getKey());
            if (propFormat == null)
                throw new IllegalArgumentException("Unknown prop: " + entry.getKey() + ": " + entry.getValue());

            bytes.addAll(writeStringAsList(entry.getKey()));
            byte[] data = propFormat.encode(entry.getValue());

            // May need to change to 4 bytes because 16MB is limiting
            for (byte b : writeU24(data.length))
                bytes.add(b);

            for (byte b : data)
                bytes.add(b);
        }

        byte[] size = writeU32(bytes.size());
        byte[] array = new byte[bytes.size() + size.length];
        System.arraycopy(size, 0, array, 0, size.length);

        for (int i = 0; i < bytes.size(); i++)
            array[i + 4] = bytes.get(i);

        return array;
    }

}
