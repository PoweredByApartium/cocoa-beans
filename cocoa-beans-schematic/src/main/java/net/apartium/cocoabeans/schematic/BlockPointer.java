package net.apartium.cocoabeans.schematic;

public final class BlockPointer extends Pointer {

    private final BlockData data;

    BlockPointer(BlockData data) {
        this.data = data;
    }

    public BlockData getData() {
        return data;
    }
}
