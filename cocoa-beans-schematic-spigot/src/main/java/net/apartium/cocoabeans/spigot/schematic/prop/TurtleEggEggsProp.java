package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TurtleEgg;

public class TurtleEggEggsProp extends IntBlockProp implements SpigotPropHandler {

    public TurtleEggEggsProp(int eggs) {
        super(eggs);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof TurtleEgg turtleEgg))
            return;

        turtleEgg.setEggs(value);
    }

}
