package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;

public class PowerablePoweredProp extends BooleanBlockProp implements SpigotPropHandler {

    public PowerablePoweredProp(boolean powered) {
        super(powered);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Powerable powerable))
            return;

        powerable.setPowered(value);
    }

}
