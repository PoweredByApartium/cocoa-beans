package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.format.BodyExtension;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Set;

/**
 * Represents a 3d schematic
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public interface Schematic {

    /**
     * Platform the schematic was created on
     * @return platform the schematic was created on
     */
    MinecraftPlatform originPlatform();

    /**
     * Get the time the schematic was created in
     * @return time the schematic was created in
     */
    Instant created();

    /**
     * Get schematic metadata
     * @return schematic metadata container
     */
    SchematicMetadata metadata();

    /**
     * Get the offset of (0,0,0) in this schematic from the player pov
     * @return schematic offset
     */
    Position offset();

    /**
     * Get the area size of this schematic
     * @return schematic size
     */
    AreaSize size();

    /**
     * Get the default axis order of this schematic
     * @return schematic default axis order
     */
    AxisOrder axisOrder();

    /**
     * Get body extensions of this schematic (like mobs etc...)
     * @return schematic body extensions
     */
    Set<BodyExtension<?>> bodyExtensions();

    /**
     * Get schematic block data for a certain position, may be nullable
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
    BlockIterator blocksIterator();

    /**
     * Iterate over schematic with specified axis order
     * @param axisOrder axis order
     * @return blocks iterator instance
     */
    BlockIterator sortedIterator(AxisOrder axisOrder);

    /**
     * Iterate over schematic with specified axis order, while reading blocks on each axis from end to start
     * @param axisOrder axis order
     * @param reverseAxis axes to read on reverse
     * @return blocks iterator instance
     * @see Schematic#sortedIterator(AxisOrder) 
     */
    BlockIterator reverseIterator(AxisOrder axisOrder, Set<Axis> reverseAxis);
    
    /**
     * Creates a builder with the existing values of this schematic
     * @return a new builder instance
     */
    SchematicBuilder toBuilder();

}