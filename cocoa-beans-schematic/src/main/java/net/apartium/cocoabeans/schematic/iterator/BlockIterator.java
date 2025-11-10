package net.apartium.cocoabeans.schematic.iterator;

import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@ApiStatus.AvailableSince("0.0.46")
public interface BlockIterator extends Iterator<BlockPlacement> {

    @Nullable Position current();

}
