package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface SpigotSchematicPlacer {

    static SpigotSchematicPlacer getInstance() {
        return CocoaSchematicInstantiator.getPlacerInstance();
    }

    void place(Block block, BlockPlacement placement);
    BlockData getBlockData(Block block);

}
