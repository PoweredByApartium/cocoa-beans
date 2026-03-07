package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;

public record RailShapeProp(Rail.Shape shape) implements BlockProp<Rail.Shape>, SpigotPropHandler {

    @Override
    public Rail.Shape value() {
        return shape;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Rail rail))
            return;

        rail.setShape(shape);
    }

}
