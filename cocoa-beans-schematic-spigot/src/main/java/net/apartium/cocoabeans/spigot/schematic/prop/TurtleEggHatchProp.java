package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TurtleEgg;

public class TurtleEggHatchProp extends IntBlockProp implements SpigotPropHandler {

    public TurtleEggHatchProp(int hatch) {
        super(hatch);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof TurtleEgg turtleEgg))
            return;

        turtleEgg.setHatch(value);
    }

}
