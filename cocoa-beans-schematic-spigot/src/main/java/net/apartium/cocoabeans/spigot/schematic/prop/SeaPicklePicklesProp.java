package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SeaPickle;

public class SeaPicklePicklesProp extends IntBlockProp implements SpigotPropHandler {

    public SeaPicklePicklesProp(int pickles) {
        super(pickles);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof SeaPickle seaPickle))
            return;

        seaPickle.setPickles(value);
    }

}
