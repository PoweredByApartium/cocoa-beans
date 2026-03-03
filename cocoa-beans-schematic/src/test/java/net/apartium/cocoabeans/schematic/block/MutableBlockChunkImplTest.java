package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static net.apartium.cocoabeans.schematic.block.BlockChunkImpl.SIZE;
import static org.junit.jupiter.api.Assertions.*;

class MutableBlockChunkImplTest {

    private static BlockData stone() {
        return new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
    }

    private static BlockData dirt() {
        return new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
    }

    private static BlockPlacement bp(int x, int y, int z) {
        return new BlockPlacement(new Position(x, y, z), stone());
    }

    private static MutableBlockChunkImpl chunk1() {
        return new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
    }

    private static MutableBlockChunkImpl chunk4() {
        return new MutableBlockChunkImpl(AxisOrder.XYZ, 4, Position.ZERO, Position.ZERO);
    }


    @Test
    void newChunkHasZeroMask() {
        assertEquals(0L, chunk1().getMask());
    }

    @Test
    void newChunkHasNoPointers() {
        assertTrue(chunk1().getPointers().isEmpty());
    }

    @Test
    void gettersReturnConstructorValues() {
        Position actualPos = new Position(5, 10, 15);
        Position chunkPos = new Position(1, 2, 3);
        MutableBlockChunkImpl chunk = new MutableBlockChunkImpl(AxisOrder.ZYX, 2, actualPos, chunkPos);

        assertEquals(AxisOrder.ZYX, chunk.getAxisOrder());
        assertEquals(2.0, chunk.getScaler());
        assertSame(actualPos, chunk.getActualPos());
        assertSame(chunkPos, chunk.getChunkPos());
    }

    @Test
    void emptyFactoryCreatesValidEmptyChunk() {
        MutableBlockChunk chunk = BlockChunk.empty();
        assertNotNull(chunk);
        assertEquals(0L, chunk.getMask());
        assertTrue(chunk.getPointers().isEmpty());
        assertNull(chunk.getBlock(new Position(0, 0, 0)));
    }

    @Test
    void setBlockReturnsTrueOnSuccess() {
        assertTrue(chunk1().setBlock(bp(0, 0, 0)));
    }

    @Test
    void setBlockReturnsFalseBeforeActualPos() {
        MutableBlockChunk chunk = chunk1();
        assertFalse(chunk.setBlock(new BlockPlacement(new Position(-1, 0, 0), stone())));
        assertFalse(chunk.setBlock(new BlockPlacement(new Position(0, -1, 0), stone())));
        assertFalse(chunk.setBlock(new BlockPlacement(new Position(0, 0, -1), stone())));
    }

    @Test
    void setAndGetSingleBlock() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(1, 2, 3));
        assertEquals(stone(), chunk.getBlock(new Position(1, 2, 3)));
    }

    @Test
    void setBlockMultiplePositions() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        chunk.setBlock(bp(1, 0, 0));
        chunk.setBlock(bp(0, 1, 0));
        chunk.setBlock(bp(0, 0, 1));
        chunk.setBlock(bp(3, 3, 3));

        assertEquals(stone(), chunk.getBlock(new Position(0, 0, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(1, 0, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(0, 1, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(0, 0, 1)));
        assertEquals(stone(), chunk.getBlock(new Position(3, 3, 3)));
    }

    @Test
    void setBlockReplacesExistingBlock() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(new BlockPlacement(new Position(2, 1, 0), stone()));
        chunk.setBlock(new BlockPlacement(new Position(2, 1, 0), dirt()));
        assertEquals(dirt(), chunk.getBlock(new Position(2, 1, 0)));
    }

    @Test
    void setBlockSameDataReturnsTrueAndKeepsBlock() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));
        assertTrue(chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone())));
        assertEquals(stone(), chunk.getBlock(new Position(0, 0, 0)));
    }

    @Test
    void pointerCountMatchesUniqueBlocksSet() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        chunk.setBlock(bp(1, 0, 0));
        chunk.setBlock(bp(3, 3, 3));
        assertEquals(3, chunk.getPointers().size());
    }

    @Test
    void getBlockReturnsNullForNeverSet() {
        MutableBlockChunkImpl chunk = chunk1();
        assertNull(chunk.getBlock(new Position(0, 0, 0)));
        assertNull(chunk.getBlock(new Position(3, 3, 3)));
    }

    @Test
    void getBlockReturnsNullBeforeActualPos() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        assertNull(chunk.getBlock(new Position(-1, 0, 0)));
        assertNull(chunk.getBlock(new Position(0, -1, 0)));
    }

    @Test
    void removeExistingBlockReturnsData() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(new BlockPlacement(new Position(1, 1, 1), stone()));
        assertEquals(stone(), chunk.removeBlock(new Position(1, 1, 1)));
    }

    @Test
    void removeBlockAfterReplaceReturnsReplacedData() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), dirt()));
        assertEquals(dirt(), chunk.removeBlock(new Position(0, 0, 0)));
    }

    @Test
    void removeNonExistentBlockReturnsNull() {
        assertNull(chunk1().removeBlock(new Position(0, 0, 0)));
    }

    @Test
    void removeBeforeActualPosReturnsNull() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        assertNull(chunk.removeBlock(new Position(-1, 0, 0)));
    }

    @Test
    void getBlockReturnsNullAfterRemove() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(2, 3, 1));
        chunk.removeBlock(new Position(2, 3, 1));
        assertNull(chunk.getBlock(new Position(2, 3, 1)));
    }

    @Test
    void maskIsZeroAfterRemovingLastBlock() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        assertNotEquals(0L, chunk.getMask());
        chunk.removeBlock(new Position(0, 0, 0));
        assertEquals(0L, chunk.getMask());
        assertTrue(chunk.getPointers().isEmpty());
    }

    @Test
    void removeMiddleOfThreeBlocks() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        chunk.setBlock(bp(1, 0, 0));
        chunk.setBlock(bp(2, 0, 0));

        chunk.removeBlock(new Position(1, 0, 0));

        assertEquals(stone(), chunk.getBlock(new Position(0, 0, 0)));
        assertNull(chunk.getBlock(new Position(1, 0, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(2, 0, 0)));
        assertEquals(2, chunk.getPointers().size());
    }

    @Test
    void removeFirstOfThreeBlocks() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        chunk.setBlock(bp(1, 0, 0));
        chunk.setBlock(bp(2, 0, 0));

        chunk.removeBlock(new Position(0, 0, 0));

        assertNull(chunk.getBlock(new Position(0, 0, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(1, 0, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(2, 0, 0)));
        assertEquals(2, chunk.getPointers().size());
    }

    @Test
    void removeLastOfThreeBlocks() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        chunk.setBlock(bp(1, 0, 0));
        chunk.setBlock(bp(2, 0, 0));

        chunk.removeBlock(new Position(2, 0, 0));

        assertEquals(stone(), chunk.getBlock(new Position(0, 0, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(1, 0, 0)));
        assertNull(chunk.getBlock(new Position(2, 0, 0)));
        assertEquals(2, chunk.getPointers().size());
    }

    @Test
    void maskBit0SetAfterSetBlockAtIndex0() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        assertTrue((chunk.getMask() & 1L) != 0);
    }

    @Test
    void maskUpdatesCorrectlyForMultipleBlocks() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        chunk.setBlock(bp(1, 0, 0));
        chunk.setBlock(bp(2, 0, 0));

        long mask = chunk.getMask();
        assertTrue((mask & 1L) != 0);
        assertTrue((mask & (1L << 1)) != 0);
        assertTrue((mask & (1L << 2)) != 0);
    }

    @Test
    void mutableReturnsNewMutableInstance() {
        MutableBlockChunkImpl original = chunk1();
        original.setBlock(bp(0, 0, 0));

        MutableBlockChunk copy = original.mutable();

        assertNotSame(original, copy);
        assertInstanceOf(MutableBlockChunk.class, copy);
        assertEquals(stone(), copy.getBlock(new Position(0, 0, 0)));
    }

    @Test
    void mutableCopyIsIndependent() {
        MutableBlockChunkImpl original = chunk1();
        original.setBlock(bp(0, 0, 0));

        MutableBlockChunk copy = original.mutable();
        copy.setBlock(new BlockPlacement(new Position(1, 0, 0), dirt()));

        assertNull(original.getBlock(new Position(1, 0, 0)));
    }

    @Test
    void immutableReturnsBlockChunkImplType() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(2, 0, 0));

        BlockChunk immutable = chunk.immutable();

        assertNotSame(chunk, immutable);
        assertInstanceOf(BlockChunkImpl.class, immutable);
        assertFalse(immutable instanceof MutableBlockChunk);
        assertEquals(stone(), immutable.getBlock(new Position(2, 0, 0)));
    }

    @Test
    void immutableCopyPreservesAllBlocks() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(0, 0, 0));
        chunk.setBlock(bp(1, 2, 3));
        chunk.setBlock(bp(3, 3, 3));

        BlockChunk immutable = chunk.immutable();

        assertEquals(stone(), immutable.getBlock(new Position(0, 0, 0)));
        assertEquals(stone(), immutable.getBlock(new Position(1, 2, 3)));
        assertEquals(stone(), immutable.getBlock(new Position(3, 3, 3)));
        assertNull(immutable.getBlock(new Position(2, 0, 0)));
    }

    @Test
    void copyConstructorFromBlockChunkCopiesData() {
        MutableBlockChunkImpl original = chunk1();
        original.setBlock(bp(0, 0, 0));
        original.setBlock(bp(2, 3, 1));

        MutableBlockChunkImpl copy = new MutableBlockChunkImpl(original);

        assertEquals(stone(), copy.getBlock(new Position(0, 0, 0)));
        assertEquals(stone(), copy.getBlock(new Position(2, 3, 1)));
        assertNull(copy.getBlock(new Position(1, 1, 1)));
    }

    @Test
    void copyConstructorIsIndependent() {
        MutableBlockChunkImpl original = chunk1();
        original.setBlock(bp(0, 0, 0));

        MutableBlockChunkImpl copy = new MutableBlockChunkImpl(original);
        copy.setBlock(new BlockPlacement(new Position(1, 0, 0), dirt()));

        assertNull(original.getBlock(new Position(1, 0, 0)));
    }

    @Test
    void sizeOfEmptyChunkIsZero() {
        assertEquals(new AreaSize(0, 0, 0), chunk1().getSizeOfEntireChunk());
    }

    @Test
    void sizeAfterPlacingOneBlock() {
        MutableBlockChunkImpl chunk = chunk1();
        chunk.setBlock(bp(2, 1, 3));
        assertEquals(new AreaSize(3, 2, 4), chunk.getSizeOfEntireChunk());
    }

    @Test
    void setAndGetWithScaler4() {
        MutableBlockChunkImpl chunk = chunk4();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));
        assertEquals(stone(), chunk.getBlock(new Position(0, 0, 0)));
    }

    @Test
    void setMultipleBlocksAcrossCellsWithScaler4() {
        MutableBlockChunkImpl chunk = chunk4();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));
        chunk.setBlock(new BlockPlacement(new Position(4, 0, 0), dirt()));
        chunk.setBlock(new BlockPlacement(new Position(8, 4, 0), stone()));

        assertEquals(stone(), chunk.getBlock(new Position(0, 0, 0)));
        assertEquals(dirt(),  chunk.getBlock(new Position(4, 0, 0)));
        assertEquals(stone(), chunk.getBlock(new Position(8, 4, 0)));
        assertNull(chunk.getBlock(new Position(1, 0, 0)));
    }

    @Test
    void removeWithScaler4ReturnsData() {
        MutableBlockChunkImpl chunk = chunk4();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));

        BlockData removed = chunk.removeBlock(new Position(0, 0, 0));

        assertEquals(stone(), removed);
        assertNull(chunk.getBlock(new Position(0, 0, 0)));
        assertEquals(0L, chunk.getMask());
    }

    @Test
    void removeWithScaler4ClearsSubchunkWhenEmpty() {
        MutableBlockChunkImpl chunk = chunk4();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));
        chunk.setBlock(new BlockPlacement(new Position(4, 0, 0), dirt()));

        chunk.removeBlock(new Position(0, 0, 0));

        assertNull(chunk.getBlock(new Position(0, 0, 0)));
        assertEquals(dirt(), chunk.getBlock(new Position(4, 0, 0)));
        assertEquals(1, chunk.getPointers().size());
    }

    @Test
    void getNonExistentBlockWithScaler4ReturnsNull() {
        MutableBlockChunkImpl chunk = chunk4();
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), stone()));
        assertNull(chunk.getBlock(new Position(4, 0, 0)));
    }

    @Test
    void fillAndVerifyAllPositionsInScaler1Chunk() {
        MutableBlockChunkImpl chunk = chunk1();

        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                for (int z = 0; z < SIZE; z++)
                    chunk.setBlock(new BlockPlacement(new Position(x, y, z), stone()));

        assertEquals(SIZE * SIZE * SIZE, chunk.getPointers().size());

        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                for (int z = 0; z < SIZE; z++)
                    assertEquals(stone(), chunk.getBlock(new Position(x, y, z)));
    }

    @Test
    void setAndRemoveAllBlocksMaskBecomesZero() {
        MutableBlockChunkImpl chunk = chunk1();

        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                for (int z = 0; z < SIZE; z++)
                    chunk.setBlock(new BlockPlacement(new Position(x, y, z), stone()));

        for (int x = 0; x < SIZE; x++)
            for (int y = 0; y < SIZE; y++)
                for (int z = 0; z < SIZE; z++)
                    assertNotNull(chunk.removeBlock(new Position(x, y, z)));

        assertEquals(0L, chunk.getMask());
        assertTrue(chunk.getPointers().isEmpty());
    }
}
