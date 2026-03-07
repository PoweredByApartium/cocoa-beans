package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Wall;

public class WallUpProp extends BooleanBlockProp implements SpigotPropHandler {

    public WallUpProp(boolean up) {
        super(up);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Wall wall))
            return;

        wall.setUp(value);
    }

}
