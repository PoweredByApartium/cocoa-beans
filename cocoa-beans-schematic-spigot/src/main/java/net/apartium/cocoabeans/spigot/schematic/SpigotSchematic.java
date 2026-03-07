package net.apartium.cocoabeans.spigot.schematic;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;

import static net.apartium.cocoabeans.spigot.Locations.toVector;

/**
 * Represents a schematic implementation for the Spigot platform.
 * <p>
 * Provides methods to paste schematics into the world using Spigot-specific operations.
 * Supports flexible axis ordering, block placement filtering, and custom block data mapping.
 * <p>
 * Usage example:
 * <pre>
 *     SpigotSchematic schematic = ...;
 *     Location origin = ...;
 *     SpigotPasteOperation operation = schematic.paste(origin);
 * </pre>
 * @see AbstractSchematic
 * @see SpigotPasteOperation
 * @see SpigotSchematicPlacer
 * @since 0.0.46
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public class SpigotSchematic extends AbstractSchematic {

    /**
     * Constructs a SpigotSchematic with explicit parameters.
     *
     * @param platform the Minecraft platform
     * @param created the creation timestamp
     * @param metadata schematic metadata
     * @param offset the offset position
     * @param size the area size
     * @param axes the axis order
     * @param iterator the block iterator
     */
    public SpigotSchematic(MinecraftPlatform platform, Instant created, SchematicMetadata metadata, Position offset, AreaSize size, AxisOrder axes, BlockIterator iterator) {
        super(platform, created, metadata, offset, size, axes, iterator);
    }

    /**
     * Constructs a SpigotSchematic from an existing schematic.
     *
     * @param schematic the schematic to copy
     */
    public SpigotSchematic(Schematic schematic) {
        super(schematic);
    }

    /**
     * Pastes the schematic at the given origin location using the default axis order.
     *
     * @param origin the origin location
     * @return the paste operation
     */
    public SpigotPasteOperation paste(Location origin) {
        return paste(origin, axisOrder());
    }

    /**
     * Pastes the schematic at the given origin location with a specified axis order.
     * Only places blocks if the current block is air.
     *
     * @param origin the origin location
     * @param axisOrder the axis order
     * @return the paste operation
     */
    public SpigotPasteOperation paste(Location origin, AxisOrder axisOrder) {
        return paste(origin, axisOrder, sortedIterator(axisOrder));
    }

    /**
     * Pastes the schematic at the given origin location with a specified axis order.
     * Only places blocks if the current block is air.
     *
     * @param origin the origin location
     * @param axisOrder the axis order
     * @param iterator the block iterator, used to determine the order of block placement
     * @return the paste operation
     */
    public SpigotPasteOperation paste(Location origin, AxisOrder axisOrder, BlockIterator iterator) {
        return new SpigotPasteOperation(
                origin.clone().add(toVector(offset)),
                iterator,
                axisOrder,
                (block, blockPlacement) -> block.getType() == Material.AIR,
                BlockPlacement::block,
                SpigotSchematicPlacer.getInstance()
        );
    }

    /**
     * Converts this schematic to a builder for further modification.
     *
     * @return a builder initialized with this schematic
     */
    @Override
    public SpigotSchematicBuilder toBuilder() {
        return new SpigotSchematicBuilder(this);
    }
}
