package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

public record BisectedHalfProp(Bisected.Half half) implements BlockProp<Bisected.Half>, SpigotPropHandler {

    @Override
    public Bisected.Half value() {
        return half;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Bisected bisected))
            return;

        bisected.setHalf(half);
    }

}
