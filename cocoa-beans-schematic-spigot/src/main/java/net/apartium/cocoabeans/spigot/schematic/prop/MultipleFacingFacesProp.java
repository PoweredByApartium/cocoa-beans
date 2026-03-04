package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

import java.util.Map;

public record MultipleFacingFacesProp(Map<BlockFace, Boolean> faces) implements BlockProp<Map<BlockFace, Boolean>>, SpigotPropHandler {

    @Override
    public Map<BlockFace, Boolean> value() {
        return faces;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof MultipleFacing multipleFacing))
            return;

        for (Map.Entry<BlockFace, Boolean> entry : faces.entrySet())
            multipleFacing.setFace(
                    entry.getKey(),
                    entry.getValue()
            );
    }

}
