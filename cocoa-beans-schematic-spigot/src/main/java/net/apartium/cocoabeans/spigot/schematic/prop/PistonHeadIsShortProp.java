package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PistonHead;

public class PistonHeadIsShortProp extends BooleanBlockProp implements SpigotPropHandler {

    public PistonHeadIsShortProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof PistonHead head))
            return;

        head.setShort(value);
    }

}
