package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SculkCatalyst;

public class SculkCatalystBloomProp extends BooleanBlockProp implements SpigotPropHandler {

    public SculkCatalystBloomProp(boolean bloom) {
        super(bloom);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof SculkCatalyst sculkCatalyst))
            return;

        sculkCatalyst.setBloom(value);
    }

}
