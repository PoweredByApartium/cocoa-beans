package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SculkShrieker;

public class SculkShriekerShrinkingProp extends BooleanBlockProp implements SpigotPropHandler {

    public SculkShriekerShrinkingProp(boolean shrieking) {
        super(shrieking);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof SculkShrieker sculkShrieker))
            return;

        sculkShrieker.setShrieking(value);
    }
}
