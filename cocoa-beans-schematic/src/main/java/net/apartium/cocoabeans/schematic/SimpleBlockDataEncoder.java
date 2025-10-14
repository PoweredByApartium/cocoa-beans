package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import org.jetbrains.annotations.ApiStatus;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.apartium.cocoabeans.schematic.utils.FileUtils.*;

@ApiStatus.AvailableSince("0.0.45")
public class SimpleBlockDataEncoder implements BlockDataEncoder {

    public static final int id = 0b1;

    private final Map<String, BlockPropFormat<?>> propFormatMap;

    public SimpleBlockDataEncoder(Map<String, BlockPropFormat<?>> formatMap) {
        this.propFormatMap = formatMap;
    }

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
                    throw new RuntimeException("Unknown prop type: " + type);

                if (props.containsKey(type))
                    throw new RuntimeException("Duplicate prop of type: " + type);

                int valueLength = readU24(din);
                byte[] valueBytes = din.readNBytes(valueLength);

                props.put(type, propFormat.decode(valueBytes));
            }

            return new GenericBlockData(namespacedKey, props);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
