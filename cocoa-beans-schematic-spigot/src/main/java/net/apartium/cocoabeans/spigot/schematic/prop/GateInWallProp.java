package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Gate;

public class GateInWallProp extends BooleanBlockProp implements SpigotPropHandler {

    public GateInWallProp(boolean value) {
        super(value);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Gate gate))
            return;

        gate.setInWall(value);
    }

}
