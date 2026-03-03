package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.format.BodyExtension;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AbstractSchematicBuilderTest {

    private static final class TestBuilder extends AbstractSchematicBuilder<DummySchematic> {

        TestBuilder() {
            super();
        }

        TestBuilder(Schematic<?> schematic) {
            super(schematic);
        }

        @Override
        public @NonNull SchematicBuilder<DummySchematic> metadata(@NonNull Function<SchematicMetadataBuilder, SchematicMetadata> block) {
            throw new UnsupportedOperationException("Not needed for unit tests");
        }

        @Override
        public DummySchematic build() {
            throw new UnsupportedOperationException("Not needed for unit tests");
        }

        List<BlockPlacement> placementsSortedXYZ() {
            List<BlockPlacement> out = new ArrayList<>();
            BlockChunkIterator it = new BlockChunkIterator(this.blockChunk);
            while (it.hasNext()) out.add(it.next());

            out.sort(Comparator
                    .comparingInt((BlockPlacement p) -> (int) p.position().getX())
                    .thenComparingInt(p -> (int) p.position().getY())
                    .thenComparingInt(p -> (int) p.position().getZ()));
            return out;
        }

        Set<Position> positions() {
            Set<Position> set = new HashSet<>();
            for (BlockPlacement p : placementsSortedXYZ()) set.add(p.position());
            return set;
        }
    }

    private static final class DummySchematic implements Schematic<DummySchematic> {
        @Override public @NonNull MinecraftPlatform originPlatform() { throw new UnsupportedOperationException(); }
        @Override public @NonNull Instant created() { throw new UnsupportedOperationException(); }
        @Override public @NonNull SchematicMetadata metadata() { throw new UnsupportedOperationException(); }
        @Override public @NonNull Position offset() { throw new UnsupportedOperationException(); }
        @Override public @NonNull AreaSize size() { throw new UnsupportedOperationException(); }
        @Override public @NonNull AxisOrder axisOrder() { throw new UnsupportedOperationException(); }
        @Override public @NonNull Set<BodyExtension<?>> bodyExtensions() { throw new UnsupportedOperationException(); }
        @Override public @NonNull BlockIterator blocksIterator() { throw new UnsupportedOperationException(); }
        @Override public @NonNull BlockIterator sortedIterator(@NonNull AxisOrder axisOrder) { throw new UnsupportedOperationException(); }
        @Override public @NonNull BlockIterator reverseIterator(@NonNull AxisOrder axisOrder, @NonNull Set<Axis> reverseAxis) { throw new UnsupportedOperationException(); }
        @Override public @NonNull SchematicBuilder<DummySchematic> toBuilder() { throw new UnsupportedOperationException(); }

        @Override
        public @Nullable BlockData getBlockData(int x, int y, int z) {
            return null;
        }

    }

    private static BlockData dirt() {
        return new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
    }

    private static BlockPlacement bp(int x, int y, int z) {
        return new BlockPlacement(new Position(x, y, z), dirt());
    }

    @Test
    void setBlock_growsSize() {
        TestBuilder b = new TestBuilder();

        assertEquals(new AreaSize(0, 0, 0), b.size);

        b.setBlock(0, 0, 0, dirt());
        assertEquals(new AreaSize(1, 1, 1), b.size);

        b.setBlock(3, 0, 0, dirt());
        assertEquals(new AreaSize(4, 1, 1), b.size);

        b.setBlock(3, 2, 5, dirt());
        assertEquals(new AreaSize(4, 3, 6), b.size);
    }


    @Test
    void rotate_invalidDegreesThrows() {
        TestBuilder b = new TestBuilder();
        b.setBlock(0, 0, 0, dirt());
        b.size(new AreaSize(4, 1, 4));

        assertThrows(IllegalArgumentException.class, () -> b.rotate(45));
        assertThrows(IllegalArgumentException.class, () -> b.rotate(1));
        assertThrows(IllegalArgumentException.class, () -> b.rotate(91));
    }

    @Test
    void rotate0_keepsPositionsSizeOffset() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(3, 0, 1));
        b.size(new AreaSize(4, 1, 4));
        b.translate(new Position(7, 0, -2));

        Set<Position> before = b.positions();
        AreaSize sizeBefore = b.size;
        Position offsetBefore = b.offset;

        b.rotate(0);

        assertEquals(before, b.positions());
        assertEquals(sizeBefore, b.size);
        assertEquals(offsetBefore, b.offset);
    }

    @Test
    void rotate90_movesBlocksAndUpdatesSizeAndOffset() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(3, 0, 1));
        b.size(new AreaSize(4, 1, 6));
        b.translate(new Position(2, 0, 10));

        b.rotate(90);

        assertEquals(Set.of(
                new Position(0, 0, 3),
                new Position(1, 0, 0)
        ), b.positions());

        assertEquals(new AreaSize(6, 1, 4), b.size);

        assertEquals(new Position(
                10,
                0,
                -4 - 2 + 1
        ), b.offset);
    }

    @Test
    void rotate180_movesBlocksAndUpdatesOffset() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(3, 0, 1));
        b.size(new AreaSize(4, 1, 6));
        b.translate(new Position(2, 0, 10));

        b.rotate(180);

        assertEquals(Set.of(
                new Position(3, 0, 5),
                new Position(0, 0, 4)
        ), b.positions());

        assertEquals(new AreaSize(4, 1, 6), b.size);

        assertEquals(new Position(
                -4 - 2 + 1,
                0,
                -6 - 10 + 1
        ), b.offset);
    }

    @Test
    void rotate270_movesBlocksAndUpdatesSizeAndOffset() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(3, 0, 1));
        b.size(new AreaSize(4, 1, 6));
        b.translate(new Position(2, 0, 10));

        b.rotate(270);

        assertEquals(Set.of(
                new Position(5, 0, 0),
                new Position(4, 0, 3)
        ), b.positions());

        assertEquals(new AreaSize(6, 1, 4), b.size);

        assertEquals(new Position(
                -4 - 10 + 2,
                0,
                2
        ), b.offset);
    }

    @Test
    void flipX_mirrorsXWithinWidth_offsetCurrentlyUnchanged() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(3, 0, 1));
        b.size(new AreaSize(4, 1, 6));
        b.translate(new Position(9, 0, 9));

        Position offsetBefore = b.offset;

        b.flip(Axis.X);

        assertEquals(Set.of(
                new Position(3, 0, 0),
                new Position(0, 0, 1)
        ), b.positions());

        assertEquals(offsetBefore, b.offset);
    }

    @Test
    void flipZ_mirrorsZWithinDepth_offsetCurrentlyUnchanged() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(3, 0, 1));
        b.size(new AreaSize(4, 1, 6));
        b.translate(new Position(9, 0, 9));

        Position offsetBefore = b.offset;

        b.flip(Axis.Z);

        assertEquals(Set.of(
                new Position(0, 0, 5),
                new Position(3, 0, 4)
        ), b.positions());

        assertEquals(offsetBefore, b.offset);
    }

    @Test
    void shift_movesBlocksAndGrowsSize() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(1, 0, 0));

        b.shift(Axis.X, 5);

        assertEquals(Set.of(
                new Position(5, 0, 0),
                new Position(6, 0, 0)
        ), b.positions());

        assertEquals(new AreaSize(7, 1, 1), b.size);
    }

    @Test
    void translateAxisOrder_keepsPositionsButChangesAxisOrder() {
        TestBuilder b = new TestBuilder();

        b.setBlock(bp(0, 0, 0));
        b.setBlock(bp(3, 0, 1));
        b.size(new AreaSize(4, 1, 6));

        Set<Position> before = b.positions();

        b.translate(AxisOrder.ZYX);

        assertEquals(before, b.positions());
        assertEquals(AxisOrder.ZYX, b.axes);
    }

    @Test
    void setBlockThrows() {
        TestBuilder testBuilder = new TestBuilder();
        BlockData dirt = dirt();

        assertThrows(IllegalArgumentException.class, () -> testBuilder.setBlock(-1, 0, 0, dirt));
        assertThrows(IllegalArgumentException.class, () -> testBuilder.setBlock(0, -1, 0, dirt));
        assertThrows(IllegalArgumentException.class, () -> testBuilder.setBlock(0, 0, -1, dirt));
    }

    @Test
    void setBlock_placementOverload_setsBlock() {
        TestBuilder b = new TestBuilder();
        b.setBlock(new BlockPlacement(new Position(1, 2, 3), dirt()));
        assertEquals(Set.of(new Position(1, 2, 3)), b.positions());
        assertEquals(new AreaSize(2, 3, 4), b.size);
    }

    @Test
    void removeBlock_removesBlock() {
        TestBuilder b = new TestBuilder();
        b.setBlock(0, 0, 0, dirt());
        b.setBlock(1, 0, 0, dirt());
        b.removeBlock(0, 0, 0);
        assertEquals(Set.of(new Position(1, 0, 0)), b.positions());
    }

    @Test
    void removeBlock_recalculatesSize() {
        TestBuilder b = new TestBuilder();
        b.setBlock(2, 3, 4, dirt());
        assertEquals(new AreaSize(3, 4, 5), b.size);
        b.removeBlock(2, 3, 4);
        assertEquals(new AreaSize(0, 0, 0), b.size);
    }

    @Test
    void translatePosition_setsOffset() {
        TestBuilder b = new TestBuilder();
        Position offset = new Position(7, -3, 2);
        b.translate(offset);
        assertEquals(offset, b.offset);
    }

    @Test
    void platform_setsValue() {
        TestBuilder b = new TestBuilder();
        MinecraftPlatform platform = new MinecraftPlatform(MinecraftVersion.V1_8_9, "spigot", "1.0");
        b.platform(platform);
        assertSame(platform, b.platform);
    }

    @Test
    void created_setsValue() {
        TestBuilder b = new TestBuilder();
        Instant now = Instant.ofEpochMilli(123456789L);
        b.created(now);
        assertEquals(now, b.created);
    }

    @Test
    void metadata_setsValue() {
        TestBuilder b = new TestBuilder();
        SchematicMetadata meta = SchematicMetadata.of();
        b.metadata(meta);
        assertSame(meta, b.metadata);
    }

    @Test
    void size_setsValue() {
        TestBuilder b = new TestBuilder();
        AreaSize size = new AreaSize(5, 3, 7);
        b.size(size);
        assertEquals(size, b.size);
    }

    @Test
    void copyConstructorFromSchematic_copiesBlocksAndProperties() {
        MutableBlockChunkImpl chunk = new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
        chunk.setBlock(new BlockPlacement(new Position(0, 0, 0), dirt()));
        chunk.setBlock(new BlockPlacement(new Position(1, 0, 0), dirt()));

        MinecraftPlatform platform = new MinecraftPlatform(MinecraftVersion.V1_8_9, "test", "1.0");
        Instant created = Instant.ofEpochMilli(1000L);
        SchematicMetadata metadata = SchematicMetadata.of();
        Position offset = new Position(5, 0, 3);
        AreaSize size = new AreaSize(2, 1, 1);

        TestSchematic source = new TestSchematic(
                platform, created, metadata, offset, size,
                AxisOrder.XYZ,
                new BlockChunkIterator(chunk)
        );

        TestBuilder copy = new TestBuilder(source);

        assertSame(platform, copy.platform);
        assertEquals(created, copy.created);
        assertSame(metadata, copy.metadata);
        assertEquals(size, copy.size);
        assertEquals(2, copy.positions().size());
        assertTrue(copy.positions().contains(new Position(0, 0, 0)));
        assertTrue(copy.positions().contains(new Position(1, 0, 0)));
    }
}