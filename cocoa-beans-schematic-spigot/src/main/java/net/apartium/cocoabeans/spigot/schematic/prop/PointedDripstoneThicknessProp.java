package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PointedDripstone;

public record PointedDripstoneThicknessProp(PointedDripstone.Thickness value) implements BlockProp<PointedDripstone.Thickness>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof PointedDripstone pointedDripstone))
            return;

        pointedDripstone.setThickness(value);
    }

}
