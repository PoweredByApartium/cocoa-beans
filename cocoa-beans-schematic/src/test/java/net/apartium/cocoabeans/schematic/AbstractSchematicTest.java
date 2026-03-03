package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AbstractSchematicTest {

    private static final MinecraftPlatform PLATFORM = new MinecraftPlatform(MinecraftVersion.V1_20, "paper", "1.0");
    private static final Instant CREATED = Instant.EPOCH;
    private static final SchematicMetadata META = SchematicMetadata.of();

    private static BlockData stone() {
        return new GenericBlockData(new NamespacedKey("minecraft", "stone"), Map.of());
    }

    private static TestSchematic emptySchematic() {
        return new TestSchematic(
                PLATFORM, CREATED, META,
                Position.ZERO, new AreaSize(0, 0, 0),
                AxisOrder.XYZ,
                new BlockChunkIterator(BlockChunk.empty())
        );
    }

    private static TestSchematic schematicWith(BlockPlacement... placements) {
        MutableBlockChunkImpl chunk = new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
        int maxX = 0, maxY = 0, maxZ = 0;
        for (BlockPlacement p : placements) {
            chunk.setBlock(p);
            maxX = Math.max(maxX, (int) p.position().getX() + 1);
            maxY = Math.max(maxY, (int) p.position().getY() + 1);
            maxZ = Math.max(maxZ, (int) p.position().getZ() + 1);
        }
        return new TestSchematic(
                PLATFORM, CREATED, META,
                Position.ZERO, new AreaSize(maxX, maxY, maxZ),
                AxisOrder.XYZ,
                new BlockChunkIterator(chunk)
        );
    }

    @Test
    void originPlatform_returnsConstructorValue() {
        assertEquals(PLATFORM, emptySchematic().originPlatform());
    }

    @Test
    void created_returnsConstructorValue() {
        assertEquals(CREATED, emptySchematic().created());
    }

    @Test
    void metadata_returnsConstructorValue() {
        assertSame(META, emptySchematic().metadata());
    }

    @Test
    void offset_returnsConstructorValue() {
        Position offset = new Position(3, 5, 7);
        TestSchematic s = new TestSchematic(
                PLATFORM, CREATED, META,
                offset, new AreaSize(1, 1, 1),
                AxisOrder.XYZ,
                new BlockChunkIterator(BlockChunk.empty())
        );
        assertSame(offset, s.offset());
    }

    @Test
    void size_returnsConstructorValue() {
        AreaSize size = new AreaSize(4, 2, 8);
        TestSchematic s = new TestSchematic(
                PLATFORM, CREATED, META,
                Position.ZERO, size,
                AxisOrder.XYZ,
                new BlockChunkIterator(BlockChunk.empty())
        );
        assertEquals(size, s.size());
    }

    @Test
    void axisOrder_returnsConstructorValue() {
        TestSchematic s = new TestSchematic(
                PLATFORM, CREATED, META,
                Position.ZERO, new AreaSize(1, 1, 1),
                AxisOrder.ZYX,
                new BlockChunkIterator(BlockChunk.empty())
        );
        assertEquals(AxisOrder.ZYX, s.axisOrder());
    }

    @Test
    void bodyExtensions_returnsEmptyForDefaultTestSchematic() {
        assertTrue(emptySchematic().bodyExtensions().isEmpty());
    }

    @Test
    void getBlockData_negativeX_returnsNull() {
        assertNull(emptySchematic().getBlockData(-1, 0, 0));
    }

    @Test
    void getBlockData_negativeY_returnsNull() {
        assertNull(emptySchematic().getBlockData(0, -1, 0));
    }

    @Test
    void getBlockData_negativeZ_returnsNull() {
        assertNull(emptySchematic().getBlockData(0, 0, -1));
    }

    @Test
    void getBlockData_outOfBoundsX_returnsNull() {
        TestSchematic s = schematicWith(new BlockPlacement(new Position(2, 0, 0), stone()));
        assertNull(s.getBlockData(3, 0, 0));
    }

    @Test
    void getBlockData_outOfBoundsY_returnsNull() {
        TestSchematic s = schematicWith(new BlockPlacement(new Position(0, 2, 0), stone()));
        assertNull(s.getBlockData(0, 3, 0));
    }

    @Test
    void getBlockData_outOfBoundsZ_returnsNull() {
        TestSchematic s = schematicWith(new BlockPlacement(new Position(0, 0, 2), stone()));
        assertNull(s.getBlockData(0, 0, 3));
    }

    @Test
    void getBlockData_validPosition_returnsBlock() {
        TestSchematic s = schematicWith(new BlockPlacement(new Position(1, 2, 3), stone()));
        BlockData result = s.getBlockData(1, 2, 3);
        assertNotNull(result);
        assertEquals("stone", result.type().key());
    }

    @Test
    void getBlockData_emptyPosition_returnsNull() {
        TestSchematic s = schematicWith(new BlockPlacement(new Position(1, 0, 0), stone()));
        assertNull(s.getBlockData(2, 0, 0));
    }

    @Test
    void blocksIterator_returnsNonNull() {
        assertNotNull(emptySchematic().blocksIterator());
    }

    @Test
    void blocksIterator_iteratesAllBlocks() {
        TestSchematic s = schematicWith(
                new BlockPlacement(new Position(0, 0, 0), stone()),
                new BlockPlacement(new Position(1, 0, 0), stone())
        );

        BlockIterator it = s.blocksIterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void sortedIterator_returnsNonNull() {
        assertNotNull(emptySchematic().sortedIterator(AxisOrder.XYZ));
    }

    @Test
    void sortedIterator_iteratesBlocks() {
        TestSchematic s = schematicWith(
                new BlockPlacement(new Position(0, 0, 0), stone()),
                new BlockPlacement(new Position(1, 0, 0), stone())
        );

        BlockIterator it = s.sortedIterator(AxisOrder.XYZ);
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void reverseIterator_returnsNonNull() {
        assertNotNull(emptySchematic().reverseIterator(AxisOrder.XYZ, Set.of()));
    }

    @Test
    void reverseIterator_withReverseAxis_returnsNonNull() {
        assertNotNull(emptySchematic().reverseIterator(AxisOrder.XYZ, Set.of(Axis.X)));
    }
}
