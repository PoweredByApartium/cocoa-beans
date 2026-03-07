package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;

public class SnowLayersProp extends IntBlockProp implements SpigotPropHandler {

    public SnowLayersProp(int layers) {
        super(layers);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Snow snow))
            return;

        snow.setLayers(value);
    }

}
