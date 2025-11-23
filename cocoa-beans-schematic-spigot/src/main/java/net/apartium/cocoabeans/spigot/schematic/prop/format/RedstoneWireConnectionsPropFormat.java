package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.RedstoneWireConnectionsProp;
import net.apartium.cocoabeans.utils.FileUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RedstoneWireConnectionsPropFormat implements BlockPropFormat<Map<BlockFace, RedstoneWire.Connection>> {

    private static final BlockFace[] BLOCK_FACES_VALUES = BlockFace.values();
    private static final RedstoneWire.Connection[] CONNECTION_VALUES = RedstoneWire.Connection.values();

    private final Function<Map<BlockFace, RedstoneWire.Connection>, BlockProp<Map<BlockFace, RedstoneWire.Connection>>> constructor;

    public static final RedstoneWireConnectionsPropFormat INSTANCE = new RedstoneWireConnectionsPropFormat();

    private RedstoneWireConnectionsPropFormat() {
        this(RedstoneWireConnectionsProp::new);
    }

    public RedstoneWireConnectionsPropFormat(Function<Map<BlockFace, RedstoneWire.Connection>, BlockProp<Map<BlockFace, RedstoneWire.Connection>>> constructor) {
        this.constructor = constructor;
    }

    @Override
    public BlockProp<Map<BlockFace, RedstoneWire.Connection>> decode(byte[] value) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
            Map<BlockFace, RedstoneWire.Connection> connections = new HashMap<>();

            while (in.available() > 0) {
                connections.put(
                        FileUtils.readEnum(in, BlockFace.class, () -> BLOCK_FACES_VALUES),
                        FileUtils.readEnum(in, RedstoneWire.Connection.class, () -> CONNECTION_VALUES)
                );
            }

            return constructor.apply(Collections.unmodifiableMap(connections));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<BlockFace, RedstoneWire.Connection> getConnectionsOrElseThrow(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null)
            throw new NullPointerException("RedstoneWireConnectionsProp prop value is null");

        if (!(value instanceof Map<?,?> map))
            throw new IllegalArgumentException("RedstoneWireConnectionsProp prop value is of wrong type");

        return (Map<BlockFace, RedstoneWire.Connection>) map;
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Map<BlockFace, RedstoneWire.Connection> connections = getConnectionsOrElseThrow(prop);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream(1 + connections.size() * 16);
        DataOutputStream out = new DataOutputStream(byteArray);

        try {
            for (Map.Entry<BlockFace, RedstoneWire.Connection> entry : connections.entrySet()) {
                BlockFace face = entry.getKey();
                RedstoneWire.Connection connection = entry.getValue();

                out.write(FileUtils.writeEnum(face));
                out.write(FileUtils.writeEnum(connection));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArray.toByteArray();
    }

}
