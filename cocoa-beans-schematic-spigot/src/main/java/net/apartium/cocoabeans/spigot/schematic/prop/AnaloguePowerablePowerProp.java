package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;

public class AnaloguePowerablePowerProp extends IntBlockProp implements SpigotPropHandler {

    public AnaloguePowerablePowerProp(int power) {
        super(power);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof AnaloguePowerable powerable))
            return;

        powerable.setPower(value);
    }

}
