package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;

import java.time.Instant;
import java.util.UUID;

@ApiStatus.AvailableSince("0.0.45")
public interface Schematic {

    UUID id();

    MinecraftPlatform platform();
    Instant created();

    /**
     * Get the time the schematic was created in
     * @return time the schematic was created in
     */
    @NonNull Instant created();

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

    BlockData getBlockData(int x, int y, int z);
    BlockIterator blocksIterator();
    BlockIterator sortedIterator(AxisOrder axisOrder);

    SchematicBuilder<?> toBuilder();

}