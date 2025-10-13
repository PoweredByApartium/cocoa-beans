package net.apartium.cocoabeans.schematic;

public final class ChunkPointer extends Pointer {

    private final BlockChunk chunk;

    public ChunkPointer(BlockChunk chunk) {
        this.chunk = chunk;
    }

    public BlockChunk getChunk() {
        return chunk;
    }
}
