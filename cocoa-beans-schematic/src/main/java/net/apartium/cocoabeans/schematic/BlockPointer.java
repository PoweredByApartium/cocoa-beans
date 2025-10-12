package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;

/* package-private */  final class BlockPointer extends Pointer {

    private final BlockData data;

    BlockPointer(BlockData data) {
        this.data = data;
    }

    public BlockData getData() {
        return data;
    }
}
