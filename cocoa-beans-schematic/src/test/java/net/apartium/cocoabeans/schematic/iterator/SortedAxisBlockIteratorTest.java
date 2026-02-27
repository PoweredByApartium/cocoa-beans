package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.iterator.SortedAxisBlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SortedAxisBlockIteratorTest {

    private static BlockData dirt() {
        return new GenericBlockData(new NamespacedKey("minecraft", "dirt"), Map.of());
    }

    private static MutableBlockChunk chunkXYZ() {
        return new MutableBlockChunkImpl(AxisOrder.XYZ, 1, Position.ZERO, Position.ZERO);
    }

    private static void set(MutableBlockChunk chunk, int x, int y, int z) {
        chunk.setBlock(new BlockPlacement(new Position(x, y, z), dirt()));
    }

    @Test
    void emptyChunk_hasNoNext_andCurrentNull() {
        MutableBlockChunk chunk = chunkXYZ();
        AreaSize size = new AreaSize(3, 3, 3);

        SortedAxisBlockIterator it = new SortedAxisBlockIterator(chunk, size, AxisOrder.XYZ);

        assertNull(it.current());
        assertFalse(it.hasNext());
    }

    @Test
    void iteratesOnlyPresentBlocks_inAxisOrderXYZ() {
        MutableBlockChunk chunk = chunkXYZ();
        AreaSize size = new AreaSize(3, 2, 3);

        set(chunk, 0, 0, 0);
        set(chunk, 2, 0, 0);
        set(chunk, 0, 1, 2);

        SortedAxisBlockIterator it = new SortedAxisBlockIterator(chunk, size, AxisOrder.XYZ);

        assertEquals(new Position(0, 0, 0), it.current());
        assertTrue(it.hasNext());

        List<Position> got = new ArrayList<>();
        while (it.hasNext()) {
            BlockPlacement p = it.next();
            got.add(p.position());
        }

        assertEquals(List.of(
                new Position(0, 0, 0),
                new Position(0, 1, 2),
                new Position(2, 0, 0)
        ), got);

        assertNull(it.current());
        assertFalse(it.hasNext());
    }

    @Test
    void reverseAxisX_iteratesFromMaxXToMinX() {
        MutableBlockChunk chunk = chunkXYZ();
        AreaSize size = new AreaSize(4, 1, 1);

        set(chunk, 0, 0, 0);
        set(chunk, 1, 0, 0);
        set(chunk, 3, 0, 0);

        SortedAxisBlockIterator it = new SortedAxisBlockIterator(
                chunk,
                size,
                AxisOrder.XYZ,
                Set.of(Axis.X)
        );

        List<Position> got = new ArrayList<>();
        while (it.hasNext()) got.add(it.next().position());

        assertEquals(List.of(
                new Position(3, 0, 0),
                new Position(1, 0, 0),
                new Position(0, 0, 0)
        ), got);
    }

    @Test
    void reverseAxisZ_iteratesFromMaxZToMinZ() {
        MutableBlockChunk chunk = chunkXYZ();
        AreaSize size = new AreaSize(1, 1, 4);

        set(chunk, 0, 0, 0);
        set(chunk, 0, 0, 2);
        set(chunk, 0, 0, 3);

        SortedAxisBlockIterator it = new SortedAxisBlockIterator(
                chunk,
                size,
                AxisOrder.XYZ,
                Set.of(Axis.Z)
        );

        List<Position> got = new ArrayList<>();
        while (it.hasNext()) got.add(it.next().position());

        assertEquals(List.of(
                new Position(0, 0, 3),
                new Position(0, 0, 2),
                new Position(0, 0, 0)
        ), got);
    }

    @Test
    void currentPointsToNextPlacement_afterEachNext() {
        MutableBlockChunk chunk = chunkXYZ();
        AreaSize size = new AreaSize(3, 1, 1);

        set(chunk, 0, 0, 0);
        set(chunk, 2, 0, 0);

        SortedAxisBlockIterator it = new SortedAxisBlockIterator(chunk, size, AxisOrder.XYZ);

        assertEquals(new Position(0, 0, 0), it.current());

        BlockPlacement first = it.next();
        assertEquals(new Position(0, 0, 0), first.position());

        assertEquals(new Position(2, 0, 0), it.current());

        BlockPlacement second = it.next();
        assertEquals(new Position(2, 0, 0), second.position());

        assertNull(it.current());
        assertFalse(it.hasNext());
    }
}