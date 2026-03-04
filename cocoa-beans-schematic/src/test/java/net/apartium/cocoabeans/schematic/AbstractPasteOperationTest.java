package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.block.GenericBlockData;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AbstractPasteOperationTest {

    private static final class SortedSeqIterator implements BlockIterator {
        private final List<BlockPlacement> list;
        private int idx = -1;

        SortedSeqIterator(List<BlockPlacement> input, AxisOrder axisOrder) {
            this.list = new ArrayList<>(input);
            this.list.sort((a, b) -> compareByAxisOrder(a.position(), b.position(), axisOrder));
        }

        @Override public boolean hasNext() { return idx + 1 < list.size(); }

        @Override public BlockPlacement next() {
            idx++;
            return list.get(idx);
        }

        @Override public Position current() {
            return idx < 0 ? null : list.get(idx).position();
        }

        private static int compareByAxisOrder(Position p1, Position p2, AxisOrder order) {
            int c;

            c = Integer.compare((int) order.getFirst().getAlong(p1), (int) order.getFirst().getAlong(p2));
            if (c != 0) return c;

            c = Integer.compare((int) order.getSecond().getAlong(p1), (int) order.getSecond().getAlong(p2));
            if (c != 0) return c;

            return Integer.compare((int) order.getThird().getAlong(p1), (int) order.getThird().getAlong(p2));
        }
    }

    private static class RecordingPasteOp extends AbstractPasteOperation {
        final List<BlockPlacement> placed = new ArrayList<>();

        RecordingPasteOp(BlockIterator iterator, AxisOrder axisOrder) {
            super(iterator, axisOrder);
        }

        @Override
        protected boolean place(@NonNull BlockPlacement placement) {
            placed.add(placement);
            return true;
        }
    }

    private static BlockPlacement p(int x, int y, int z) {
        return new BlockPlacement(
                new Position(x, y, z),
                new GenericBlockData(
                        new NamespacedKey("minecraft", "dirt"),
                        Map.of()
                )
        );
    }

    @Test
    void hasNextAndGetNextPosition_delegatesToIterator() {
        AxisOrder order = AxisOrder.XYZ;
        var placements = List.of(p(0, 0, 0), p(1, 0, 0));

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        assertTrue(op.hasNext());
        assertNull(op.getNextPosition());

        BlockPlacement first = it.next();
        assertEquals(first.position(), op.getNextPosition());
        assertTrue(op.hasNext());

        BlockPlacement second = it.next();
        assertEquals(second.position(), op.getNextPosition());
        assertFalse(op.hasNext());
    }


    @Test
    void performAll_placesEverything() {
        AxisOrder order = AxisOrder.XYZ;
        var placements = List.of(p(2,0,0), p(0,0,0), p(1,0,0));

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        PasteResult r = op.performAll();

        assertEquals(3, r.blockPlaces());
        assertEquals(0, r.leftOver());

        assertEquals(List.of(
                p(0,0,0),
                p(1,0,0),
                p(2,0,0)
        ), op.placed);
    }

    @Test
    void performAll_skipsWhenPlaceReturnsFalse() {
        AxisOrder order = AxisOrder.XYZ;
        var placements = List.of(p(0,0,0), p(1,0,0), p(2,0,0));

        var it = new SortedSeqIterator(placements, order);

        var op = new RecordingPasteOp(it, order) {
            int calls = 0;
            @Override protected boolean place(@NonNull BlockPlacement placement) {
                calls++;
                if (calls == 2) return false;
                placed.add(placement);
                return true;
            }
        };

        PasteResult r = op.performAll();

        assertEquals(2, r.blockPlaces());
        assertEquals(0, r.leftOver());
        assertEquals(List.of(p(0,0,0), p(2,0,0)), op.placed);
    }

    @Test
    void performAllOnDualAxis_placesUntilFirstAxisChanges_includingFirstDifferentBlock_dueToCurrentCheck() {
        AxisOrder order = AxisOrder.XYZ;

        var placements = List.of(
                p(0,0,0),
                p(0,5,0),
                p(0,9,9),
                p(1,0,0),
                p(1,1,1)
        );

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        PasteResult r = op.performAllOnDualAxis();

        assertEquals(4, r.blockPlaces());
        assertEquals(0, r.leftOver());

        assertEquals(List.of(
                p(0,0,0),
                p(0,5,0),
                p(0,9,9),
                p(1,0,0)
        ), op.placed);

        assertTrue(op.hasNext());
        assertEquals(new Position(1,0,0), op.getNextPosition());
    }

    @Test
    void performAllOnSingleAxis_placesUntilFirstOrSecondAxisChanges_includingFirstDifferentBlock_dueToCurrentCheck() {
        AxisOrder order = AxisOrder.XYZ;

        var placements = List.of(
                p(0,0,0),
                p(0,0,5),
                p(0,0,9),
                p(0,1,0),
                p(0,1,1)
        );

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        PasteResult r = op.performAllOnSingleAxis();

        assertEquals(4, r.blockPlaces());
        assertEquals(0, r.leftOver());

        assertEquals(List.of(
                p(0,0,0),
                p(0,0,5),
                p(0,0,9),
                p(0,1,0)
        ), op.placed);

        assertTrue(op.hasNext());
    }

    @Test
    void advanceAllAxis_respectsBudget_andReturnsRemaining() {
        AxisOrder order = AxisOrder.XYZ;
        var placements = List.of(p(0,0,0), p(1,0,0), p(2,0,0), p(3,0,0));

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        PasteResult r = op.advanceAllAxis(2);

        assertEquals(2, r.blockPlaces());
        assertEquals(0, r.leftOver());
        assertEquals(List.of(p(0,0,0), p(1,0,0)), op.placed);
    }

    @Test
    void advanceAllAxis_stopsAtEnd_andReturnsUnconsumed() {
        AxisOrder order = AxisOrder.XYZ;
        var placements = List.of(p(0,0,0), p(1,0,0));

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        PasteResult r = op.advanceAllAxis(5);

        assertEquals(2, r.blockPlaces());
        assertEquals(3, r.leftOver());
    }


    @Test
    void advanceOnDualAxis_stopsOnAxisChange_includingFirstDifferentBlock_dueToCurrentCheck() {
        AxisOrder order = AxisOrder.XYZ;

        var placements = List.of(
                p(0,0,0),
                p(0,0,1),
                p(1,0,0),
                p(1,0,1)
        );

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        PasteResult r = op.advanceOnDualAxis(10);

        assertEquals(3, r.blockPlaces());
        assertEquals(7, r.leftOver());

        assertEquals(List.of(
                p(0,0,0),
                p(0,0,1),
                p(1,0,0)
        ), op.placed);

        assertTrue(op.hasNext());
    }

    @Test
    void advanceOnSingleAxis_stopsOnAxisChange_includingFirstDifferentBlock_dueToCurrentCheck() {
        AxisOrder order = AxisOrder.XYZ;

        var placements = List.of(
                p(0,0,0),
                p(0,0,1),
                p(0,1,0),
                p(0,1,1)
        );

        var it = new SortedSeqIterator(placements, order);
        var op = new RecordingPasteOp(it, order);

        PasteResult r = op.advanceOnSingleAxis(10);

        assertEquals(3, r.blockPlaces());
        assertEquals(7, r.leftOver());

        assertEquals(List.of(
                p(0,0,0),
                p(0,0,1),
                p(0,1,0)
        ), op.placed);

        assertTrue(op.hasNext());
    }
}