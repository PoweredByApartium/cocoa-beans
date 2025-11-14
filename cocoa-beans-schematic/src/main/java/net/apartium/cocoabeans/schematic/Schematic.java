package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Represents a 3d schematic
 */
@ApiStatus.AvailableSince("0.0.46")
public interface Schematic {

    /**
     * Platform the schematic was created on
     * @return platform the schematic was created on
     */
    @NonNull MinecraftPlatform originPlatform();

    /**
     * Get the time the schematic was created in
     * @return time the schematic was created in
     */
    @NonNull Instant created();

    /**
     * Get schematic metadata
     * @return schematic metadata container
     */
    @NonNull SchematicMetadata metadata();

    /**
     * Get the offset of (0,0,0) in this schematic from the player pov
     * @return schematic offset
     */
    @NonNull Position offset();

    /**
     * Get the area size of this schematic
     * @return schematic size
     */
    @NonNull AreaSize size();

    /**
     * Get the default axis order of this schematic
     * @return schematic default axis order
     */
    @NonNull AxisOrder axisOrder();

    /**
     * Get schematic block data for certain position, may be nullable
     * @param x position x
     * @param y position y
     * @param z position z
     * @return a block data instance, or null if no block is found
     */
    @Nullable BlockData getBlockData(int x, int y, int z);

    /**
     * Iterate over schematic with the data order.
     * Note this iterator does not provide a sorting guarantee.
     * @return blocks iterator instance
     */
    @NonNull BlockIterator blocksIterator();

    /**
     * Iterate over schematic with specified axis order
     * @param axisOrder axis order
     * @return blocks iterator instance
     */
    @NonNull BlockIterator sortedIterator(@NonNull AxisOrder axisOrder);

    /**
     * Creates a builder with the existing values of this schematic
     * @return a new builder instance
     */
    @NonNull SchematicBuilder<?> toBuilder();

}