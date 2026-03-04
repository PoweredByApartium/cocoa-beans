package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;

public class SaplingStageProp extends IntBlockProp implements SpigotPropHandler {

    public SaplingStageProp(int stage) {
        super(stage);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Sapling sapling))
            return;

        sapling.setStage(value);
    }

}
