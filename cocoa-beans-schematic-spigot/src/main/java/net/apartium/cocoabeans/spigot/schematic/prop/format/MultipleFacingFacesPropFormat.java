package net.apartium.cocoabeans.spigot.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.format.BlockPropFormat;
import net.apartium.cocoabeans.spigot.schematic.prop.MultipleFacingFacesProp;
import net.apartium.cocoabeans.utils.BufferUtils;
import org.bukkit.block.BlockFace;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public record MultipleFacingFacesPropFormat(
        Function<Map<BlockFace, Boolean>, BlockProp<Map<BlockFace, Boolean>>> constructor
) implements BlockPropFormat<Map<BlockFace, Boolean>> {

    public static final MultipleFacingFacesPropFormat INSTANCE = new MultipleFacingFacesPropFormat();

    private MultipleFacingFacesPropFormat() {
        this(MultipleFacingFacesProp::new);
    }

    @Override
    public BlockProp<Map<BlockFace, Boolean>> decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));

        return constructor.apply(Collections.unmodifiableMap(BufferUtils.readMapOfEnumBoolean(
                in,
                BlockFace.class, BlockFace::values
        )));
    }

    private Map<BlockFace, Boolean> getMultipleFacingFacesOrElseThrow(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null)
            throw new NullPointerException("MultipleFacingFacesPropFormat prop value is null");

        if (!(value instanceof Map<?, ?> map))
            throw new IllegalArgumentException("MultipleFacingFacesPropFormat prop value is not a Map");

        Map<BlockFace, Boolean> result = new EnumMap<>(BlockFace.class);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object k = entry.getKey();
            Object v = entry.getValue();

            if (!(k instanceof BlockFace blockFace)) {
                throw new IllegalArgumentException(
                        "Map key is not a BlockFace: " + k + " (" + k.getClass().getName() + ")"
                );
            }
            if (!(v instanceof Boolean valueOfBoolean)) {
                throw new IllegalArgumentException(
                        "Map value is not a Boolean: " + v + " (" + v.getClass().getName() + ")"
                );
            }

            result.put(blockFace, valueOfBoolean);
        }

        return result;
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Map<BlockFace, Boolean> multipleFacingFaces = getMultipleFacingFacesOrElseThrow(prop);
        return BufferUtils.writeMapOfEnumBoolean(multipleFacingFaces);
    }

}
