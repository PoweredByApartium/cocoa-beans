package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a block placement
 * @param position block position
 * @param block block data
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public record BlockPlacement(Position position, BlockData block) {

}
