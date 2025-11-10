package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Campfire;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class CampfireSignalFireProp extends BooleanBlockProp implements SpigotPropHandler {

    public CampfireSignalFireProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Campfire campfire))
            return;

        campfire.setSignalFire(value);
    }

}
