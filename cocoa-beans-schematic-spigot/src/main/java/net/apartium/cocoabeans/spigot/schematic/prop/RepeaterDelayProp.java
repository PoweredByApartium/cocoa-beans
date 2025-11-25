package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Repeater;

public class RepeaterDelayProp extends IntBlockProp implements SpigotPropHandler {

    public RepeaterDelayProp(int delay) {
        super(delay);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Repeater repeater))
            return;

        repeater.setDelay(value);
    }
}
