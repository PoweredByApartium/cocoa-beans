package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import org.bukkit.block.Block;

public interface SpigotSchematicPlacer {

    static SpigotSchematicPlacer getInstance() {
        return CocoaSchematicInstantiator.getPlacerInstance();
    }

    void place(Block block, BlockPlacement placement);
    BlockData getBlockData(Block block);

}
