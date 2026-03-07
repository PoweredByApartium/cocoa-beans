package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;

public class PistonExtendedProp extends BooleanBlockProp implements SpigotPropHandler {

    public PistonExtendedProp(boolean extended) {
        super(extended);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Piston piston))
            return;

        piston.setExtended(value);
    }

}
