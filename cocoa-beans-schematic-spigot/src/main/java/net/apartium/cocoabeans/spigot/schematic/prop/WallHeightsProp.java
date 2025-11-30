package net.apartium.cocoabeans.spigot.schematic.prop;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Wall;

import java.util.Map;

public record WallHeightsProp(Map<BlockFace, Wall.Height> heights) implements BlockProp<Map<BlockFace, Wall.Height>>, SpigotPropHandler {

    @Override
    public Map<BlockFace, Wall.Height> value() {
        return heights;
    }

    @Override
    public void update(BlockData blockData) {
        if (!(blockData instanceof Wall wall))
            return;

        for (Map.Entry<BlockFace, Wall.Height> heightEntry : heights.entrySet())
            wall.setHeight(heightEntry.getKey(), heightEntry.getValue());
    }

}
