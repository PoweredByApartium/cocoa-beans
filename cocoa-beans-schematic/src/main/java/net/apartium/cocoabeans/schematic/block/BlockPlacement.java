package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public record BlockPlacement(Position position, BlockData block) {

}
