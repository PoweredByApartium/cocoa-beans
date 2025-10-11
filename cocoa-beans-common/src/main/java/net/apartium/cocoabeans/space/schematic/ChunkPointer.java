package net.apartium.cocoabeans.space.schematic;

/* package-private */ final class ChunkPointer extends Pointer {

    private final BlockChunk chunk;

    ChunkPointer(BlockChunk chunk) {
        this.chunk = chunk;
    }

    public BlockChunk getChunk() {
        return chunk;
    }
}
