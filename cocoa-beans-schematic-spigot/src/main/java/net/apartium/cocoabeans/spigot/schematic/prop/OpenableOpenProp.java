package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;

public class OpenableOpenProp extends BooleanBlockProp implements SpigotPropHandler {

    public OpenableOpenProp(boolean open) {
        super(open);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Openable openable))
            return;

        openable.setOpen(value);
    }

}
