package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

public class WaterloggedProp extends BooleanBlockProp implements SpigotPropHandler {

    public WaterloggedProp(boolean waterlogged) {
        super(waterlogged);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Waterlogged waterlogged))
            return;

        waterlogged.setWaterlogged(value);
    }

}
