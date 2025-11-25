package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Scaffolding;

public class ScaffoldingDistanceProp extends IntBlockProp implements SpigotPropHandler {

    public ScaffoldingDistanceProp(int distance) {
        super(distance);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Scaffolding scaffolding))
            return;

        scaffolding.setDistance(value);
    }
}
