package net.apartium.cocoabeans.schematic.block;

import org.jetbrains.annotations.ApiStatus;

// todo internal?
@ApiStatus.AvailableSince("0.0.46")
public final class BlockPointer extends Pointer {

    private final BlockData data;

    BlockPointer(BlockData data) {
        this.data = data;
    }

    public BlockData getData() {
        return data;
    }
}
