package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Hopper;

public class HopperEnabledProp extends BooleanBlockProp implements SpigotPropHandler {

    public HopperEnabledProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Hopper hopper))
            return;

        hopper.setEnabled(value);
    }

}
