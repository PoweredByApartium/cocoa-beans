package net.apartium.cocoabeans.space.iterator;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CubeShellIteratorTest {


    @Test
    void customMinMax() {
        CubeShellIterator iterator = new CubeShellIterator(0, 100);

        assertTrue(iterator.hasNext());
        Set<Position> seen = new HashSet<>();

        while (iterator.hasNext()) {
            Position next = iterator.next();
            assertTrue(seen.add(next), "Duplicate position: " + next);

            assertTrue(next.getX() >= 0 && next.getX() <= 100, "X out of bounds: " + next);
            assertTrue(next.getY() >= 0 && next.getY() <= 100, "Y out of bounds: " + next);
            assertTrue(next.getZ() >= 0 && next.getZ() <= 100, "Z out of bounds: " + next);
        }

        assertFalse(iterator.hasNext());
        assertEquals(60002, seen.size());

        iterator = new CubeShellIterator(50, 100);

        assertTrue(iterator.hasNext());
        seen.clear();

        while (iterator.hasNext()) {
            Position next = iterator.next();
            assertTrue(seen.add(next), "Duplicate position: " + next);

            assertTrue(next.getX() >= 50 && next.getX() <= 100, "X out of bounds: " + next);
            assertTrue(next.getY() >= 50 && next.getY() <= 100, "Y out of bounds: " + next);
            assertTrue(next.getZ() >= 50 && next.getZ() <= 100, "Z out of bounds: " + next);
        }

        assertFalse(iterator.hasNext());
        assertEquals(15002, seen.size());
    }

    @Test
    void size0() {
        CubeShellIterator iterator = new CubeShellIterator(0);

        assertTrue(iterator.hasNext());
        assertEquals(new Position(0, 0, 0), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void size1() {
        CubeShellIterator iterator = new CubeShellIterator(1);

        assertTrue(iterator.hasNext());

        List<Position> positions = new ArrayList<>();
        Set<Position> seen = new HashSet<>();

        while (iterator.hasNext()) {
            Position next = iterator.next();
            positions.add(next);
            assertFalse(seen.contains(next));
            seen.add(next);
        }

        assertFalse(iterator.hasNext());
        assertEquals(26, positions.size());
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 100})
    void shellSizes(int radius) {
        CubeShellIterator iterator = new CubeShellIterator(radius);

        Set<Position> seen = new HashSet<>();

        while (iterator.hasNext()) {
            Position next = iterator.next();

            assertTrue(seen.add(next), "Duplicate position: " + next);

            assertTrue(isInsideBounds(next, radius), "Out of bounds: " + next);
            assertTrue(isOnShell(next, radius), "Not on shell: " + next);
        }

        assertFalse(iterator.hasNext());
        assertEquals(cubeShellSize(radius), seen.size(), "Wrong size for radius " + radius);
    }

    private boolean isInsideBounds(Position p, int radius) {
        return p.getX() >= -radius && p.getX() <= radius
                && p.getY() >= -radius && p.getY() <= radius
                && p.getZ() >= -radius && p.getZ() <= radius;
    }

    private boolean isOnShell(Position p, int radius) {
        return Math.abs(p.getX()) == radius
                || Math.abs(p.getY()) == radius
                || Math.abs(p.getZ()) == radius;
    }

    @Test
    void nullAxisOrderThrows() {
        assertThrows(NullPointerException.class, () -> new CubeShellIterator(null, 0, 1));
    }

    @Test
    void minLargerThanMaxThrows() {
        assertThrows(RuntimeException.class, () -> new CubeShellIterator(5, 3));
    }

    @Test
    void nextAfterExhaustionSize0() {
        CubeShellIterator iterator = new CubeShellIterator(0);
        iterator.next();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void nextAfterExhaustion() {
        CubeShellIterator iterator = new CubeShellIterator(2);
        while (iterator.hasNext()) iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @ParameterizedTest
    @EnumSource(AxisOrder.class)
    void allAxisOrdersShellIsCorrect(AxisOrder axisOrder) {
        int radius = 3;
        CubeShellIterator iterator = new CubeShellIterator(axisOrder, -radius, radius);
        Set<Position> seen = new HashSet<>();

        while (iterator.hasNext()) {
            Position next = iterator.next();
            assertTrue(seen.add(next), "Duplicate: " + next);
            assertTrue(isInsideBounds(next, radius), "Out of bounds: " + next);
            assertTrue(isOnShell(next, radius), "Not on shell: " + next);
        }

        assertEquals(cubeShellSize(radius), seen.size());
    }

    @ParameterizedTest
    @EnumSource(AxisOrder.class)
    void allAxisOrdersProduceSamePositions(AxisOrder axisOrder) {
        Set<Position> xyzPositions = collectAll(new CubeShellIterator(AxisOrder.XYZ, -2, 3));
        Set<Position> ordered = collectAll(new CubeShellIterator(axisOrder, -2, 3));
        assertEquals(xyzPositions, ordered);
    }

    @Test
    void negativeMinMax() {
        CubeShellIterator iterator = new CubeShellIterator(-5, -2);
        Set<Position> seen = new HashSet<>();

        while (iterator.hasNext()) {
            Position next = iterator.next();
            assertTrue(seen.add(next), "Duplicate: " + next);
            assertTrue(next.getX() >= -5 && next.getX() <= -2, "X out of bounds: " + next);
            assertTrue(next.getY() >= -5 && next.getY() <= -2, "Y out of bounds: " + next);
            assertTrue(next.getZ() >= -5 && next.getZ() <= -2, "Z out of bounds: " + next);
            assertTrue(next.getX() == -5 || next.getX() == -2
                    || next.getY() == -5 || next.getY() == -2
                    || next.getZ() == -5 || next.getZ() == -2, "Not on shell: " + next);
        }

        // side=4, shell=4^3 - 2^3 = 64 - 8 = 56
        assertEquals(56, seen.size());
    }

    @Test
    void minEqualsMaxNonZero() {
        CubeShellIterator iterator = new CubeShellIterator(5, 5);
        assertTrue(iterator.hasNext());
        assertEquals(new Position(5, 5, 5), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void axisOrderAffectsTraversalOrder() {
        CubeShellIterator xyzIterator = new CubeShellIterator(AxisOrder.XYZ, 0, 3);
        CubeShellIterator xzyIterator = new CubeShellIterator(AxisOrder.XZY, 0, 3);

        // First position is the same for both: (0, 0, 0)
        assertEquals(new Position(0, 0, 0), xyzIterator.next());
        assertEquals(new Position(0, 0, 0), xzyIterator.next());

        // Second position differs based on axis order:
        // XYZ face 0: position(0, a=0, b=1) → x=0, y=0, z=1
        // XZY face 0: position(0, a=0, b=1) → x=0, z=0, y=1 → (0, 1, 0)
        assertEquals(new Position(0, 0, 1), xyzIterator.next());
        assertEquals(new Position(0, 1, 0), xzyIterator.next());
    }

    private Set<Position> collectAll(CubeShellIterator iterator) {
        Set<Position> positions = new HashSet<>();
        while (iterator.hasNext()) positions.add(iterator.next());
        return positions;
    }

    private int cubeShellSize(int radius) {
        if (radius == 0)
            return 1;

        int side = 2 * radius + 1;
        int inner = side - 2;
        return side * side * side - inner * inner * inner;
    }

}
