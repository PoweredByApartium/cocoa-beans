package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.BlockData;

public class AttachableAttachedProp extends BooleanBlockProp implements SpigotPropHandler {

    public AttachableAttachedProp(boolean attached) {
        super(attached);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Attachable attachable))
            return;

        attachable.setAttached(value);
    }

}
