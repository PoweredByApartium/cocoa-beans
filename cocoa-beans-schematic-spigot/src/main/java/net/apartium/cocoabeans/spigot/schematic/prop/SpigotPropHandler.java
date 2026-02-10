package net.apartium.cocoabeans.spigot.schematic.prop;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.ApiStatus;

/**
 * Handler interface for updating properties of {@link BlockData} instances in Spigot.
 * <p>
 * Implementations of this interface are responsible for modifying the state or properties
 * of a given {@link BlockData} object according to custom logic. This is typically used
 * in schematic processing or block manipulation scenarios where block properties need to
 * be dynamically updated.
 * </p>
 *
 * @see net.apartium.cocoabeans.schematic.prop.BlockProp
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public interface SpigotPropHandler {

    /**
     * Updates the given {@link BlockData} instance with custom property changes.
     * <p>
     * Implementations should modify the provided {@code blockData} as needed.
     * </p>
     *
     * @param blockData the {@link BlockData} to update
     */
    void update(BlockData blockData);

}
