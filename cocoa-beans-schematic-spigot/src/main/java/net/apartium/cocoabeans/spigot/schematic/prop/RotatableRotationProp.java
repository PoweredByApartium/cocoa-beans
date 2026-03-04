package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;

public record RotatableRotationProp(BlockFace rotation) implements BlockProp<BlockFace>, SpigotPropHandler {

    @Override
    public BlockFace value() {
        return rotation;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Rotatable rotatable))
            return;

        rotatable.setRotation(rotation);
    }

}
