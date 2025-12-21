package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.FlippableProp;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SlabTypeProp(Slab.Type type) implements BlockProp<Slab.Type>, SpigotPropHandler, FlippableProp<Slab.Type> {

    @Override
    public Slab.Type value() {
        return type;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Slab slab))
            return;

        slab.setType(type);
    }

    @Override
    public Slab.Type flip(NamespacedKey type, Axis axis) {
        if (axis != Axis.Y)
            return this.type;

        return switch(this.type) {
            case TOP -> Slab.Type.BOTTOM;
            case BOTTOM -> Slab.Type.TOP;
            default -> this.type;
        };
    }
}
