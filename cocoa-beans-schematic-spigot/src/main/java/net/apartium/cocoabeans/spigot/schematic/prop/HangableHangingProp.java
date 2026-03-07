package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BooleanBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Hangable;

public class HangableHangingProp extends BooleanBlockProp implements SpigotPropHandler {

    public HangableHangingProp(boolean hanging) {
        super(hanging);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Hangable hangable))
            return;

        hangable.setHanging(value);
    }

}
