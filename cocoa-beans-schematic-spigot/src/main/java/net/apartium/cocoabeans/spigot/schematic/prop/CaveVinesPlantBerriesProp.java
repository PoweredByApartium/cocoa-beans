package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public class CaveVinesPlantBerriesProp extends BooleanBlockProp implements SpigotPropHandler {

    public CaveVinesPlantBerriesProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof CaveVinesPlant caveVinesPlant))
            return;

        caveVinesPlant.setBerries(value);
    }

}
