package net.apartium.cocoabeans.schematic.block;

import org.jetbrains.annotations.ApiStatus;

// todo internal?
@ApiStatus.AvailableSince("0.0.46")
public final class ChunkPointer extends Pointer {

    private final BlockChunk chunk;

    public ChunkPointer(BlockChunk chunk) {
        this.chunk = chunk;
    }

    public BlockChunk getChunk() {
        return chunk;
    }
}
