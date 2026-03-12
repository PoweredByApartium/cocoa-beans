package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Dispenser;

public class DispenserTriggeredProp extends BooleanBlockProp implements SpigotPropHandler {

    public DispenserTriggeredProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Dispenser dispenser))
            return;

        dispenser.setTriggered(value);
    }

}
