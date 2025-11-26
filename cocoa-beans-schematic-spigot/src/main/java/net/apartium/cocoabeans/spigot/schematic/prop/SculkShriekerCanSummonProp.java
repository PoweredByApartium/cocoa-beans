package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SculkShrieker;

public class SculkShriekerCanSummonProp extends BooleanBlockProp implements SpigotPropHandler {

    public SculkShriekerCanSummonProp(boolean canSummon) {
        super(canSummon);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof SculkShrieker sculkShrieker))
            return;

        sculkShrieker.setCanSummon(value);
    }

}
