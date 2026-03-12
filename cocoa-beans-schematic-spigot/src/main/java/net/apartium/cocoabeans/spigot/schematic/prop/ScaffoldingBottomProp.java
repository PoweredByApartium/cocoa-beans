package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Scaffolding;

public class ScaffoldingBottomProp extends BooleanBlockProp implements SpigotPropHandler {

    public ScaffoldingBottomProp(boolean bottom) {
        super(bottom);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Scaffolding scaffolding))
            return;

        scaffolding.setBottom(value);
    }

}
