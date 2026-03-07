package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Repeater;

public class RepeaterLockedProp extends BooleanBlockProp implements SpigotPropHandler {

    public RepeaterLockedProp(boolean locked) {
        super(locked);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Repeater repeater))
            return;

        repeater.setLocked(value);
    }
}
