package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

public class LeavesDistanceProp extends IntBlockProp implements SpigotPropHandler {

    public LeavesDistanceProp(int distance) {
        super(distance);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Leaves leaves))
            return;

        leaves.setDistance(value);
    }

}
