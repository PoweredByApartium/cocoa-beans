package net.apartium.cocoabeans.space.axis;

import net.apartium.cocoabeans.space.Position;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class AxisIteratorTest {

    @Test
    void iteratesInXYZOrder() {
        AxisIterator iterator = new AxisIterator(
                AxisOrder.XYZ,
                new Position(0, 0, 0),
                new Position(1, 1, 1),
                1
        );

        List<Position> positions = new ArrayList<>();
        while (iterator.hasNext()) {
            positions.add(iterator.next());
        }

        List<Position> expected = List.of(
                new Position(0, 0, 0),
                new Position(0, 0, 1),
                new Position(0, 1, 0),
                new Position(0, 1, 1),
                new Position(1, 0, 0),
                new Position(1, 0, 1),
                new Position(1, 1, 0),
                new Position(1, 1, 1)
        );
        assertEquals(expected, positions);
    }

    @Test
    void iteratesInZYXOrder() {
        AxisIterator iterator = new AxisIterator(
                AxisOrder.ZYX,
                new Position(0, 0, 0),
                new Position(1, 1, 1),
                1
        );

        List<Position> positions = new ArrayList<>();
        while (iterator.hasNext()) {
            positions.add(iterator.next());
        }

        List<Position> expected = List.of(
                new Position(0, 0, 0),
                new Position(1, 0, 0),
                new Position(0, 1, 0),
                new Position(1, 1, 0),
                new Position(0, 0, 1),
                new Position(1, 0, 1),
                new Position(0, 1, 1),
                new Position(1, 1, 1)
        );
        assertEquals(expected, positions);
    }

    @Test
    void stepMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new AxisIterator(
                AxisOrder.XYZ,
                new Position(0, 0, 0),
                new Position(1, 1, 1),
                0
        ));
        assertThrows(IllegalArgumentException.class, () -> new AxisIterator(
                AxisOrder.XYZ,
                new Position(0, 0, 0),
                new Position(1, 1, 1),
                -1
        ));
    }

    @Test
    void nextThrowsWhenExhausted() {
        AxisIterator iterator = new AxisIterator(
                AxisOrder.XYZ,
                new Position(0, 0, 0),
                new Position(0, 0, 0),
                1
        );

        assertTrue(iterator.hasNext());
        assertEquals(new Position(0, 0, 0), iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}
