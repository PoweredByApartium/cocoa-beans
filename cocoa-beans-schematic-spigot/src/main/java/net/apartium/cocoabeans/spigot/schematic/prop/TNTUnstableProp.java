package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TNT;

public class TNTUnstableProp extends BooleanBlockProp implements SpigotPropHandler {

    public TNTUnstableProp(boolean unstable) {
        super(unstable);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof TNT tnt))
            return;

        tnt.setUnstable(value);
    }

}
