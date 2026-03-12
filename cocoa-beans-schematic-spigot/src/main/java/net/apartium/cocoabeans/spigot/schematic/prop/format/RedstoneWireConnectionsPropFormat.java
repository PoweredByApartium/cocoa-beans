package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.RedstoneWireConnectionsProp;
import net.apartium.cocoabeans.utils.BufferUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;

import java.io.*;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class RedstoneWireConnectionsPropFormat implements BlockPropFormat<Map<BlockFace, RedstoneWire.Connection>> {

    public static final RedstoneWireConnectionsPropFormat INSTANCE = new RedstoneWireConnectionsPropFormat();

    private final Function<Map<BlockFace, RedstoneWire.Connection>, BlockProp<Map<BlockFace, RedstoneWire.Connection>>> constructor;

    private RedstoneWireConnectionsPropFormat() {
        this(RedstoneWireConnectionsProp::new);
    }

    public RedstoneWireConnectionsPropFormat(Function<Map<BlockFace, RedstoneWire.Connection>, BlockProp<Map<BlockFace, RedstoneWire.Connection>>> constructor) {
        this.constructor = constructor;
    }

    @Override
    public BlockProp<Map<BlockFace, RedstoneWire.Connection>> decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));

        return constructor.apply(Collections.unmodifiableMap(BufferUtils.readMapOfEnums(
                in,
                BlockFace.class, BlockFace::values,
                RedstoneWire.Connection.class, RedstoneWire.Connection::values
        )));
    }

    private Map<BlockFace, RedstoneWire.Connection> getConnectionsOrElseThrow(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null)
            throw new NullPointerException("RedstoneWireConnectionsProp prop value is null");

        if (!(value instanceof Map<?,?> map))
            throw new IllegalArgumentException("RedstoneWireConnectionsProp prop value is of wrong type");

        Map<BlockFace, RedstoneWire.Connection> result = new EnumMap<>(BlockFace.class);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object k = entry.getKey();
            Object v = entry.getValue();

            if (!(k instanceof BlockFace blockFace))
                throw new IllegalArgumentException(
                        "Map key is not a BlockFace: " + k + " (" + k.getClass().getName() + ")"
                );

            if (!(v instanceof RedstoneWire.Connection connection))
                throw new IllegalArgumentException(
                        "Map value is not a RedstoneWire.Connection: " + v + " (" + v.getClass().getName() + ")"
                );


            result.put(blockFace, connection);
        }

        return result;
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Map<BlockFace, RedstoneWire.Connection> connections = getConnectionsOrElseThrow(prop);
        return BufferUtils.writeMapOfEnums(connections);
    }

}
