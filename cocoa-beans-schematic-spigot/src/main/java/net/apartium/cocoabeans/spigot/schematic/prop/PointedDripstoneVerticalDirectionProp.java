package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PointedDripstone;

public record PointedDripstoneVerticalDirectionProp(BlockFace value) implements BlockProp<BlockFace>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof PointedDripstone pointedDripstone))
            return;

        pointedDripstone.setVerticalDirection(value);
    }

}
