package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BrewingStand;

public record BrewingStandBottlesProp(int[] value) implements BlockProp<int[]>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof BrewingStand brewingStand))
            return;

        for (int bottle : value)
            brewingStand.setBottle(bottle, true);
    }

}
