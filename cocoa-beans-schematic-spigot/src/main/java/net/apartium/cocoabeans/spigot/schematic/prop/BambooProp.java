package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bamboo;


public record BambooProp(Bamboo.Leaves value) implements BlockProp<Bamboo.Leaves>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Bamboo bamboo))
            return;

        bamboo.setLeaves(value);
    }
}
