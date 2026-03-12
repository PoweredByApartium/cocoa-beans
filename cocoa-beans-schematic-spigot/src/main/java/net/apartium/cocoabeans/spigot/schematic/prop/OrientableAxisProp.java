package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.Axis;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;

public record OrientableAxisProp(Axis axis) implements BlockProp<Axis>, SpigotPropHandler {

    @Override
    public Axis value() {
        return axis;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Orientable orientable))
            return;

        orientable.setAxis(axis);
    }

}
