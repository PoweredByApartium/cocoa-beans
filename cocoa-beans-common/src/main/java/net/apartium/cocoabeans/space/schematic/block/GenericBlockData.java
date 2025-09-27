package net.apartium.cocoabeans.space.schematic.block;

import net.apartium.cocoabeans.space.schematic.BlockData;
import net.apartium.cocoabeans.space.schematic.BlockProp;
import net.apartium.cocoabeans.space.schematic.NamespacedKey;

import java.util.Collections;
import java.util.Map;

public record GenericBlockData(
        NamespacedKey type,
        Map<String, BlockProp<?>> props
) implements BlockData {

    public GenericBlockData(NamespacedKey type, Map<String, BlockProp<?>> props) {
        this.type = type;
        this.props = Collections.unmodifiableMap(props);
    }
}
