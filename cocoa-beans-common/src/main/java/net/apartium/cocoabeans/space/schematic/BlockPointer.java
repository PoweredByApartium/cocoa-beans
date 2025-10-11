package net.apartium.cocoabeans.space.schematic;

/* package-private */  final class BlockPointer extends Pointer {

    private final BlockData data;

    BlockPointer(BlockData data) {
        this.data = data;
    }

    public BlockData getData() {
        return data;
    }
}
