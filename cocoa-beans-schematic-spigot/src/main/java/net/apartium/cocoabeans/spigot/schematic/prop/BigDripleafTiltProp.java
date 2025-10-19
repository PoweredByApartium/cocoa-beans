package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BigDripleaf;

public record BigDripleafTiltProp(BigDripleaf.Tilt value) implements BlockProp<BigDripleaf.Tilt>, SpigotPropHandler {

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof BigDripleaf bigDripleaf))
            return;

        bigDripleaf.setTilt(value);
    }

}
