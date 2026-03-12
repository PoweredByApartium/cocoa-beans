package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;

public class SnowableSnowyProp extends BooleanBlockProp implements SpigotPropHandler {

    public SnowableSnowyProp(boolean snowy) {
        super(snowy);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Snowable snowable))
            return;

        snowable.setSnowy(value);
    }

}
