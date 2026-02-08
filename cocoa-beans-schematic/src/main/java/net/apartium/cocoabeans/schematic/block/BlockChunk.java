package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;

public interface BlockChunk {

    static MutableBlockChunk empty() {
        return new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
    }

    // todo make not array
    Pointer[] getPointers();

    BlockData getBlock(Position pos);

    long getMask();

    double getScaler();

    Position getActualPos();

    Position getChunkPos();

    AxisOrder getAxisOrder();

    AreaSize getSizeOfEntireChunk();

    MutableBlockChunk mutable();

    BlockChunk immutable();
}
