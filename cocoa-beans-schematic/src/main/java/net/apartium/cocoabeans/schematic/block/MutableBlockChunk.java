package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.Position;

public interface MutableBlockChunk extends BlockChunk {

    boolean setBlock(BlockPlacement placement);
    BlockData removeBlock(Position position);

}
