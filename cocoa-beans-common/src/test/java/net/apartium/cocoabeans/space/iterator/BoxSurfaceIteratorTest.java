package net.apartium.cocoabeans.space.iterator;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoxSurfaceIteratorTest {

    @Test
    void customPositionMinMax() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(AxisOrder.XYZ, Position.ZERO, new Position(5, 4, 3));

        Set<Position> seen = new HashSet<>();

        while (iterator.hasNext()) {
            Position next = iterator.next();
            assertTrue(seen.add(next), "Duplicate position: " + next);

            assertTrue(next.getX() >= 0 && next.getX() <= 5, "X out of bounds: " + next);
            assertTrue(next.getY() >= 0 && next.getY() <= 4, "Y out of bounds: " + next);
            assertTrue(next.getZ() >= 0 && next.getZ() <= 3, "Z out of bounds: " + next);
        }

        assertFalse(iterator.hasNext());

        assertEquals(96, seen.size());
    }

    @Test
    void customMinMax() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(0, 100);

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

        iterator = new BoxSurfaceIterator(50, 100);

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
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(0);

        assertTrue(iterator.hasNext());
        assertEquals(new Position(0, 0, 0), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void size1() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(1);

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
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(radius);

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
        assertThrows(NullPointerException.class, () -> new BoxSurfaceIterator(null, 0, 1));
    }

    @Test
    void minLargerThanMaxThrows() {
        assertThrows(RuntimeException.class, () -> new BoxSurfaceIterator(5, 3));
    }

    @Test
    void nextAfterExhaustionSize0() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(0);
        iterator.next();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void nextAfterExhaustion() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(2);
        while (iterator.hasNext()) iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @ParameterizedTest
    @EnumSource(AxisOrder.class)
    void allAxisOrdersShellIsCorrect(AxisOrder axisOrder) {
        int radius = 3;
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(axisOrder, -radius, radius);
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
        Set<Position> xyzPositions = collectAll(new BoxSurfaceIterator(AxisOrder.XYZ, -2, 3));
        Set<Position> ordered = collectAll(new BoxSurfaceIterator(axisOrder, -2, 3));
        assertEquals(xyzPositions, ordered);
    }

    @Test
    void negativeMinMax() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(-5, -2);
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
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(5, 5);
        assertTrue(iterator.hasNext());
        assertEquals(new Position(5, 5, 5), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void axisOrderAffectsTraversalOrder() {
        BoxSurfaceIterator xyzIterator = new BoxSurfaceIterator(AxisOrder.XYZ, 0, 3);
        BoxSurfaceIterator xzyIterator = new BoxSurfaceIterator(AxisOrder.XZY, 0, 3);

        // First position is the same for both: (0, 0, 0)
        assertEquals(new Position(0, 0, 0), xyzIterator.next());
        assertEquals(new Position(0, 0, 0), xzyIterator.next());

        // Second position differs based on axis order:
        // XYZ face 0: position(0, a=0, b=1) → x=0, y=0, z=1
        // XZY face 0: position(0, a=0, b=1) → x=0, z=0, y=1 → (0, 1, 0)
        assertEquals(new Position(0, 0, 1), xyzIterator.next());
        assertEquals(new Position(0, 1, 0), xzyIterator.next());

        assertEquals(new Position(0, 0, 2), xyzIterator.next());
        assertEquals(new Position(0, 0, 3), xyzIterator.next());

        for (int x = 0; x <= 3; x += 3) {
            for (int i = x == 0 ? 1 : 0 ; i <= 3; i++)
                for (int j = 0; j <= 3; j++)
                    assertEquals(new Position(x, i, j), xyzIterator.next());
        }

    }

    @Test
    void negativeRadiusThrows() {
        assertThrows(RuntimeException.class, () -> new BoxSurfaceIterator(-1));
    }

    @Test
    void nullAxisOrderWithPositionThrows() {
        Position max = new Position(1, 1, 1);
        assertThrows(NullPointerException.class, () -> new BoxSurfaceIterator(null, Position.ZERO, max));
    }

    @Test
    void maxLessThanMinOnOneAxisThrows() {
        Position min = new Position(0, 0, 0);
        Position max = new Position(5, -1, 3);
        assertThrows(RuntimeException.class, () -> new BoxSurfaceIterator(AxisOrder.XYZ, min, max));
    }

    @Test
    void customPositionMinMaxAllOnShell() {
        Position min = Position.ZERO;
        Position max = new Position(5, 4, 3);
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(AxisOrder.XYZ, min, max);

        while (iterator.hasNext()) {
            Position next = iterator.next();
            boolean onShell = next.getX() == min.getX() || next.getX() == max.getX()
                    || next.getY() == min.getY() || next.getY() == max.getY()
                    || next.getZ() == min.getZ() || next.getZ() == max.getZ();
            assertTrue(onShell, "Position not on shell: " + next);
        }
    }

    @Test
    void forEachRemainingConsumesAll() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(2);
        Set<Position> seen = new HashSet<>();
        iterator.forEachRemaining(seen::add);
        assertFalse(iterator.hasNext());
        assertEquals(cubeShellSize(2), seen.size());
    }

    @Test
    void forEachRemainingAfterPartialConsumption() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(2);
        Position first = iterator.next();
        Set<Position> remaining = new HashSet<>();
        iterator.forEachRemaining(remaining::add);
        assertFalse(remaining.contains(first), "forEachRemaining should not re-yield already consumed position");
        assertEquals(cubeShellSize(2) - 1, remaining.size());
    }

    @Test
    void size1ShellPositionsOnBoundary() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(1);
        while (iterator.hasNext()) {
            Position next = iterator.next();
            assertTrue(isOnShell(next, 1), "Position not on shell: " + next);
        }
    }

    @Test
    void minEqualsMaxWithAxisOrder() {
        for (AxisOrder axisOrder : AxisOrder.values()) {
            BoxSurfaceIterator iterator = new BoxSurfaceIterator(axisOrder, 7, 7);
            assertTrue(iterator.hasNext());
            assertEquals(new Position(7, 7, 7), iterator.next());
            assertFalse(iterator.hasNext());
        }
    }

    // peek tests

    @Test
    void peekReturnsSameValueAsNextWithoutAdvancing() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(1);

        Position peeked = iterator.peek();
        assertTrue(iterator.hasNext());
        assertEquals(peeked, iterator.next());
    }

    @Test
    void peekCalledMultipleTimesReturnsSamePosition() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(1);

        Position first  = iterator.peek();
        Position second = iterator.peek();
        Position third  = iterator.peek();

        assertEquals(first, second);
        assertEquals(second, third);
        assertEquals(first, iterator.next());
    }

    @Test
    void peekThrowsWhenExhausted() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(0);
        iterator.next();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::peek);
    }

    @Test
    void peekOnSingleElementIterator() {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(0);
        assertEquals(new Position(0, 0, 0), iterator.peek());
        assertTrue(iterator.hasNext());
        assertEquals(new Position(0, 0, 0), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @ParameterizedTest
    @EnumSource(AxisOrder.class)
    void peekAndNextAreConsistentForAllAxisOrders(AxisOrder axisOrder) {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(axisOrder, 0, 2);

        while (iterator.hasNext()) {
            Position peeked = iterator.peek();
            assertEquals(peeked, iterator.next(), "peek() and next() diverged for " + axisOrder);
        }

        assertThrows(NoSuchElementException.class, iterator::peek);
    }

    // next() traversal-order tests per AxisOrder

    /**
     * For a unit box (0,0,0)→(1,1,1) the first four positions come from face 0
     * (the min-face of the primary axis). The exact coordinates depend on which
     * axis is primary and how the secondary/tertiary axes map to u/v.
     *
     * <pre>
     * AxisOrder | primary | u-axis | v-axis | positions (face 0)
     * ----------+---------+--------+--------+-----------------------------------
     * XYZ       |  X=0    |  Y     |  Z     | (0,0,0) (0,0,1) (0,1,0) (0,1,1)
     * XZY       |  X=0    |  Z     |  Y     | (0,0,0) (0,1,0) (0,0,1) (0,1,1)
     * YXZ       |  Y=0    |  X     |  Z     | (0,0,0) (0,0,1) (1,0,0) (1,0,1)
     * YZX       |  Y=0    |  Z     |  X     | (0,0,0) (1,0,0) (0,0,1) (1,0,1)
     * ZXY       |  Z=0    |  X     |  Y     | (0,0,0) (0,1,0) (1,0,0) (1,1,0)
     * ZYX       |  Z=0    |  Y     |  X     | (0,0,0) (1,0,0) (0,1,0) (1,1,0)
     * </pre>
     */
    static Stream<Arguments> firstFacePositionsPerAxisOrder() {
        return Stream.of(
                Arguments.of(AxisOrder.XYZ, List.of(
                        new Position(0, 0, 0), new Position(0, 0, 1),
                        new Position(0, 1, 0), new Position(0, 1, 1))),
                Arguments.of(AxisOrder.XZY, List.of(
                        new Position(0, 0, 0), new Position(0, 1, 0),
                        new Position(0, 0, 1), new Position(0, 1, 1))),
                Arguments.of(AxisOrder.YXZ, List.of(
                        new Position(0, 0, 0), new Position(0, 0, 1),
                        new Position(1, 0, 0), new Position(1, 0, 1))),
                Arguments.of(AxisOrder.YZX, List.of(
                        new Position(0, 0, 0), new Position(1, 0, 0),
                        new Position(0, 0, 1), new Position(1, 0, 1))),
                Arguments.of(AxisOrder.ZXY, List.of(
                        new Position(0, 0, 0), new Position(0, 1, 0),
                        new Position(1, 0, 0), new Position(1, 1, 0))),
                Arguments.of(AxisOrder.ZYX, List.of(
                        new Position(0, 0, 0), new Position(1, 0, 0),
                        new Position(0, 1, 0), new Position(1, 1, 0)))
        );
    }

    @ParameterizedTest
    @MethodSource("firstFacePositionsPerAxisOrder")
    void nextTraversalOrderMatchesAxisOrderOnFirstFace(AxisOrder axisOrder, List<Position> expectedFirstFace) {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(axisOrder, new Position(0, 0, 0), new Position(1, 1, 1));

        List<Position> actual = new ArrayList<>();
        for (int i = 0; i < expectedFirstFace.size(); i++) {
            assertTrue(iterator.hasNext(), "Iterator exhausted too early at step " + i);
            actual.add(iterator.next());
        }

        assertEquals(expectedFirstFace, actual, "First-face traversal order wrong for " + axisOrder);
    }

    @ParameterizedTest
    @EnumSource(AxisOrder.class)
    void peekMatchesNextOnFirstFaceForAllAxisOrders(AxisOrder axisOrder) {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(axisOrder, new Position(0, 0, 0), new Position(1, 1, 1));

        // consume exactly the first 4 positions (face 0 of a unit box) with peek/next round-trip
        for (int i = 0; i < 4; i++) {
            assertTrue(iterator.hasNext());
            Position peeked = iterator.peek();
            assertEquals(peeked, iterator.next(), "peek/next mismatch at step " + i + " for " + axisOrder);
        }
    }

    // e2e – radius-1 box: exact 26-position traversal order per AxisOrder

    /**
     * Full traversal order for a [-1,1]^3 box (26 surface positions) for every AxisOrder.
     * <p>
     * Face layout (v increments first inside each face):
     *   face 0/1 – min/max of the primary axis; u=secondary [-1..1], v=tertiary [-1..1]  → 9 each
     *   face 2/3 – min/max of secondary axis;   u=primary [0..0],    v=tertiary [-1..1]  → 3 each
     *   face 4/5 – min/max of tertiary axis;     u=primary [0..0],    v=secondary [0..0] → 1 each
     */
    static Stream<Arguments> radius1ExactOrders() {
        return Stream.of(
            // XYZ  primary=X  u=Y  v=Z
            Arguments.of(AxisOrder.XYZ, List.of(
                // face 0  x=-1
                pos(-1,-1,-1), pos(-1,-1, 0), pos(-1,-1, 1),
                pos(-1, 0,-1), pos(-1, 0, 0), pos(-1, 0, 1),
                pos(-1, 1,-1), pos(-1, 1, 0), pos(-1, 1, 1),
                // face 1  x=+1
                pos( 1,-1,-1), pos( 1,-1, 0), pos( 1,-1, 1),
                pos( 1, 0,-1), pos( 1, 0, 0), pos( 1, 0, 1),
                pos( 1, 1,-1), pos( 1, 1, 0), pos( 1, 1, 1),
                // face 2  y=-1  u=x[0]  v=z[-1..1]
                pos( 0,-1,-1), pos( 0,-1, 0), pos( 0,-1, 1),
                // face 3  y=+1
                pos( 0, 1,-1), pos( 0, 1, 0), pos( 0, 1, 1),
                // face 4  z=-1
                pos( 0, 0,-1),
                // face 5  z=+1
                pos( 0, 0, 1)
            )),
            // XZY  primary=X  u=Z  v=Y
            Arguments.of(AxisOrder.XZY, List.of(
                // face 0  x=-1
                pos(-1,-1,-1), pos(-1, 0,-1), pos(-1, 1,-1),
                pos(-1,-1, 0), pos(-1, 0, 0), pos(-1, 1, 0),
                pos(-1,-1, 1), pos(-1, 0, 1), pos(-1, 1, 1),
                // face 1  x=+1
                pos( 1,-1,-1), pos( 1, 0,-1), pos( 1, 1,-1),
                pos( 1,-1, 0), pos( 1, 0, 0), pos( 1, 1, 0),
                pos( 1,-1, 1), pos( 1, 0, 1), pos( 1, 1, 1),
                // face 2  z=-1  u=x[0]  v=y[-1..1]
                pos( 0,-1,-1), pos( 0, 0,-1), pos( 0, 1,-1),
                // face 3  z=+1
                pos( 0,-1, 1), pos( 0, 0, 1), pos( 0, 1, 1),
                // face 4  y=-1
                pos( 0,-1, 0),
                // face 5  y=+1
                pos( 0, 1, 0)
            )),
            // YXZ  primary=Y  u=X  v=Z
            Arguments.of(AxisOrder.YXZ, List.of(
                // face 0  y=-1
                pos(-1,-1,-1), pos(-1,-1, 0), pos(-1,-1, 1),
                pos( 0,-1,-1), pos( 0,-1, 0), pos( 0,-1, 1),
                pos( 1,-1,-1), pos( 1,-1, 0), pos( 1,-1, 1),
                // face 1  y=+1
                pos(-1, 1,-1), pos(-1, 1, 0), pos(-1, 1, 1),
                pos( 0, 1,-1), pos( 0, 1, 0), pos( 0, 1, 1),
                pos( 1, 1,-1), pos( 1, 1, 0), pos( 1, 1, 1),
                // face 2  x=-1  u=y[0]  v=z[-1..1]
                pos(-1, 0,-1), pos(-1, 0, 0), pos(-1, 0, 1),
                // face 3  x=+1
                pos( 1, 0,-1), pos( 1, 0, 0), pos( 1, 0, 1),
                // face 4  z=-1
                pos( 0, 0,-1),
                // face 5  z=+1
                pos( 0, 0, 1)
            )),
            // YZX  primary=Y  u=Z  v=X
            Arguments.of(AxisOrder.YZX, List.of(
                // face 0  y=-1
                pos(-1,-1,-1), pos( 0,-1,-1), pos( 1,-1,-1),
                pos(-1,-1, 0), pos( 0,-1, 0), pos( 1,-1, 0),
                pos(-1,-1, 1), pos( 0,-1, 1), pos( 1,-1, 1),
                // face 1  y=+1
                pos(-1, 1,-1), pos( 0, 1,-1), pos( 1, 1,-1),
                pos(-1, 1, 0), pos( 0, 1, 0), pos( 1, 1, 0),
                pos(-1, 1, 1), pos( 0, 1, 1), pos( 1, 1, 1),
                // face 2  z=-1  u=y[0]  v=x[-1..1]
                pos(-1, 0,-1), pos( 0, 0,-1), pos( 1, 0,-1),
                // face 3  z=+1
                pos(-1, 0, 1), pos( 0, 0, 1), pos( 1, 0, 1),
                // face 4  x=-1
                pos(-1, 0, 0),
                // face 5  x=+1
                pos( 1, 0, 0)
            )),
            // ZXY  primary=Z  u=X  v=Y
            Arguments.of(AxisOrder.ZXY, List.of(
                // face 0  z=-1
                pos(-1,-1,-1), pos(-1, 0,-1), pos(-1, 1,-1),
                pos( 0,-1,-1), pos( 0, 0,-1), pos( 0, 1,-1),
                pos( 1,-1,-1), pos( 1, 0,-1), pos( 1, 1,-1),
                // face 1  z=+1
                pos(-1,-1, 1), pos(-1, 0, 1), pos(-1, 1, 1),
                pos( 0,-1, 1), pos( 0, 0, 1), pos( 0, 1, 1),
                pos( 1,-1, 1), pos( 1, 0, 1), pos( 1, 1, 1),
                // face 2  x=-1  u=z[0]  v=y[-1..1]
                pos(-1,-1, 0), pos(-1, 0, 0), pos(-1, 1, 0),
                // face 3  x=+1
                pos( 1,-1, 0), pos( 1, 0, 0), pos( 1, 1, 0),
                // face 4  y=-1
                pos( 0,-1, 0),
                // face 5  y=+1
                pos( 0, 1, 0)
            )),
            // ZYX  primary=Z  u=Y  v=X
            Arguments.of(AxisOrder.ZYX, List.of(
                // face 0  z=-1
                pos(-1,-1,-1), pos( 0,-1,-1), pos( 1,-1,-1),
                pos(-1, 0,-1), pos( 0, 0,-1), pos( 1, 0,-1),
                pos(-1, 1,-1), pos( 0, 1,-1), pos( 1, 1,-1),
                // face 1  z=+1
                pos(-1,-1, 1), pos( 0,-1, 1), pos( 1,-1, 1),
                pos(-1, 0, 1), pos( 0, 0, 1), pos( 1, 0, 1),
                pos(-1, 1, 1), pos( 0, 1, 1), pos( 1, 1, 1),
                // face 2  y=-1  u=z[0]  v=x[-1..1]
                pos(-1,-1, 0), pos( 0,-1, 0), pos( 1,-1, 0),
                // face 3  y=+1
                pos(-1, 1, 0), pos( 0, 1, 0), pos( 1, 1, 0),
                // face 4  x=-1
                pos(-1, 0, 0),
                // face 5  x=+1
                pos( 1, 0, 0)
            ))
        );
    }

    @ParameterizedTest
    @MethodSource("radius1ExactOrders")
    void e2eRadius1ExactOrderAllAxisOrders(AxisOrder axisOrder, List<Position> expected) {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(axisOrder, -1, 1);

        List<Position> actual = new ArrayList<>();
        while (iterator.hasNext())
            actual.add(iterator.next());

        assertEquals(26, actual.size(), "Expected 26 surface positions for " + axisOrder);
        assertEquals(expected, actual, "Traversal order wrong for " + axisOrder);
    }

    @ParameterizedTest
    @MethodSource("radius1ExactOrders")
    void e2eRadius1PeekMatchesNextAllAxisOrders(AxisOrder axisOrder, List<Position> expected) {
        BoxSurfaceIterator iterator = new BoxSurfaceIterator(axisOrder, -1, 1);

        for (int i = 0; i < expected.size(); i++) {
            assertTrue(iterator.hasNext(), "Exhausted early at step " + i + " for " + axisOrder);
            assertEquals(expected.get(i), iterator.peek(), "peek() wrong at step " + i + " for " + axisOrder);
            assertEquals(expected.get(i), iterator.next(), "next() wrong at step " + i + " for " + axisOrder);
        }

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::peek);
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    private static Position pos(int x, int y, int z) {
        return new Position(x, y, z);
    }

    private Set<Position> collectAll(BoxSurfaceIterator iterator) {
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
