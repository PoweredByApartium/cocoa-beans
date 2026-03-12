package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.IntBlockProp;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

public class AgeableAgeProp extends IntBlockProp implements SpigotPropHandler {

    public AgeableAgeProp(int age) {
        super(age);
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Ageable ageable))
            return;

        ageable.setAge(value);
    }

}
