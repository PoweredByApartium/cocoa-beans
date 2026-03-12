package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;

/**
 * Interface for placing schematic blocks in Spigot.
 * <p>
 * Provides methods to place blocks according to schematic data and retrieve block data from the world.
 * </p>
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public interface SpigotSchematicPlacer {

    /**
     * Returns the singleton instance of {@link SpigotSchematicPlacer}.
     * <p>
     * This instance is provided by {@link CocoaSchematicInstantiator}.
     * </p>
     * @return the schematic placer instance
     */
    static SpigotSchematicPlacer getInstance() {
        return CocoaSchematicInstantiator.getPlacerInstance();
    }

    /**
     * Places a block in the world according to the given {@link BlockPlacement}.
     *
     * @param block the Bukkit block to place
     * @param placement the placement data describing how to place the block
     */
    void place(Block block, BlockPlacement placement);

    /**
     * Retrieves schematic block data from the given Bukkit block.
     *
     * @param block the Bukkit block
     * @return the schematic block data
     */
    BlockData getBlockData(Block block);

}
