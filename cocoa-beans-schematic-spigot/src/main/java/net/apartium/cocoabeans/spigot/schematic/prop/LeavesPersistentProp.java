package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;

public class LeavesPersistentProp extends BooleanBlockProp implements SpigotPropHandler {

    public LeavesPersistentProp(boolean persistent) {
        super(persistent);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Leaves leaves))
            return;

        leaves.setPersistent(value);
    }

}
