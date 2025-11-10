package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public record CakeBitesProp(int bites) implements BlockProp<Integer>, SpigotPropHandler {

    @Override
    public Integer value() {
        return bites;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Cake cake))
            return;

        cake.setBites(bites);
    }

}
