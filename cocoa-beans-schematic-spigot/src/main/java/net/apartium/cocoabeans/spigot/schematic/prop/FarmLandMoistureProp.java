package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;

public class FarmLandMoistureProp extends IntBlockProp implements SpigotPropHandler {

    public FarmLandMoistureProp(int value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Farmland farmland))
            return;

        farmland.setMoisture(value);
    }

}
