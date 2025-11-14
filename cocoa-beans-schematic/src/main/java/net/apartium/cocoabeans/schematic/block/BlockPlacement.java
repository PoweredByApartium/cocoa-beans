package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a block placement
 * @param position block position
 * @param block block data
 */
@ApiStatus.AvailableSince("0.0.46")
public record BlockPlacement(Position position, BlockData block) {

}
