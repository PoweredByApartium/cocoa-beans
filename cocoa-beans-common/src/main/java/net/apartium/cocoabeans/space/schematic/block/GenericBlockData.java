package net.apartium.cocoabeans.space.schematic.block;

import net.apartium.cocoabeans.space.schematic.BlockData;
import net.apartium.cocoabeans.space.schematic.BlockProp;
import net.apartium.cocoabeans.space.schematic.NamespacedKey;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public record GenericBlockData(
        NamespacedKey type,
        Map<String, BlockProp<?>> props
) implements BlockData {

    public GenericBlockData(NamespacedKey type, Map<String, BlockProp<?>> props) {
        this.type = type;
        this.props = Collections.unmodifiableMap(props);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GenericBlockData blockData = (GenericBlockData) o;
        return Objects.equals(type, blockData.type) && Objects.deepEquals(props, blockData.props);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, props);
    }
}
