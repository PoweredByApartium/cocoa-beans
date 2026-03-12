package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;

public class RespawnAnchorChargesProp extends IntBlockProp implements SpigotPropHandler {

    public RespawnAnchorChargesProp(int charges) {
        super(charges);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof RespawnAnchor respawnAnchor))
            return;

        respawnAnchor.setCharges(value);
    }

}
