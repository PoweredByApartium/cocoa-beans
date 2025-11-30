package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.WallHeightsProp;
import net.apartium.cocoabeans.utils.FileUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Wall;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WallHeightsPropFormat implements BlockPropFormat<Map<BlockFace, Wall.Height>> {

    public static final WallHeightsPropFormat INSTANCE = new WallHeightsPropFormat();

    private final Function<Map<BlockFace, Wall.Height>, BlockProp<Map<BlockFace, Wall.Height>>> constructor;

    private WallHeightsPropFormat() {
        this(WallHeightsProp::new);
    }

    public WallHeightsPropFormat(Function<Map<BlockFace, Wall.Height>, BlockProp<Map<BlockFace, Wall.Height>>> constructor) {
        this.constructor = constructor;
    }

    @Override
    public BlockProp<Map<BlockFace, Wall.Height>> decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));

        return constructor.apply(Collections.unmodifiableMap(FileUtils.readMapOfEnums(
                in,
                BlockFace.class, BlockFace::values,
                Wall.Height.class, Wall.Height::values
        )));
    }

    private Map<BlockFace, Wall.Height> getWallHeightsOrElseThrow(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null)
            throw new NullPointerException("WalLHeightsPropFormat prop value is null");

        if (!(value instanceof Map<?, ?> map))
            throw new IllegalArgumentException("WalLHeightsPropFormat prop value is not a Map");

        Map<BlockFace, Wall.Height> result = new HashMap<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object k = entry.getKey();
            Object v = entry.getValue();

            if (!(k instanceof BlockFace blockFace)) {
                throw new IllegalArgumentException(
                        "Map key is not a BlockFace: " + k + " (" + k.getClass().getName() + ")"
                );
            }
            if (!(v instanceof Wall.Height height)) {
                throw new IllegalArgumentException(
                        "Map value is not a Wall.Height: " + v + " (" + v.getClass().getName() + ")"
                );
            }

            result.put(blockFace, height);
        }

        return result;
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Map<BlockFace, Wall.Height> wallHeights = getWallHeightsOrElseThrow(prop);
        return FileUtils.writeMapOfEnums(wallHeights);
    }

}
