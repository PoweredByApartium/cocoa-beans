package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@ApiStatus.AvailableSince("0.0.46")
public record GenericBlockData(NamespacedKey type, Map<String, BlockProp<?>> props) implements BlockData {

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

}
