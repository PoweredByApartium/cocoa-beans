package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Beehive;

public class BeeHiveHoneyLevelProp extends IntBlockProp implements SpigotPropHandler {

    public BeeHiveHoneyLevelProp(int value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Beehive beehive))
            return;

        beehive.setHoneyLevel(value);
    }
}
