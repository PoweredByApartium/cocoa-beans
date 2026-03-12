package net.apartium.cocoabeans.schematic.block;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public abstract sealed class Pointer permits BlockPointer, ChunkPointer {

}
