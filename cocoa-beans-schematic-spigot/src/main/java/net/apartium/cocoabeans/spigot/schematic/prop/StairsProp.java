package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;

public record StairsProp(Stairs.Shape value) implements BlockProp<Stairs.Shape>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Stairs stairs))
            return;

        stairs.setShape(value);
    }
}
