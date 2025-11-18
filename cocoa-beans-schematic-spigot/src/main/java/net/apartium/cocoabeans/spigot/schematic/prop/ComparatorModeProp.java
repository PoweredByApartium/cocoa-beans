package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Comparator;

public record ComparatorModeProp(Comparator.Mode value) implements BlockProp<Comparator.Mode>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Comparator comparator))
            return;

        comparator.setMode(value);
    }

}
