package net.apartium.cocoabeans.schematic.block;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public sealed abstract class Pointer permits BlockPointer, ChunkPointer {

}
