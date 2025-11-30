package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;

public class LightableLitProp extends BooleanBlockProp implements SpigotPropHandler {

    public LightableLitProp(boolean lit) {
        super(lit);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Lightable lightable))
            return;

        lightable.setLit(value);
    }

}
