package net.apartium.cocoabeans.structs;

import net.apartium.cocoabeans.space.ImmutablePosition;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Rotation;
import net.apartium.cocoabeans.space.Transform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    void copy() {
        Position position = new Position(1, 2, 3);
        Position copy = position.copy();
        assertEquals(position, copy);
    }

    @Test
    void fromDirection() {
        Position position = Position.fromDirection(0, 0);
        assertEquals(-0.0, position.getX());
        assertEquals(0, position.getY());
        assertEquals(1, position.getZ());

        position = Position.fromDirection(45, 0);
        assertEquals(-0.7071067811865475, position.getX()); // -Math.sqrt(2)/2
        assertEquals(0, position.getY());
        assertEquals(0.7071067811865476, position.getZ()); // Math.sqrt(2)/2

        position = Position.fromDirection(90, 0);
        assertEquals(-1, position.getX());
        assertEquals(0, position.getY());
        assertTrue(Math.abs(position.getZ()) < 0.0000001);

        position = Position.fromDirection(270, 0);
        assertEquals(1, position.getX());
        assertEquals(0, position.getY());
        assertTrue(Math.abs(position.getZ()) < 0.0000001);

        position = Position.fromDirection(0, 45);
        assertEquals(-0.0, position.getX());
        assertEquals(0.7071067811865475, position.getY());
        assertEquals(0.7071067811865476, position.getZ());
    }

    @Test
    void addAndSubtract() {
        Position position = new Position(1, 2, 3);
        assertEquals(new Position(2, 4, 6), position.add(new Position(1, 2, 3)));
        assertEquals(new Position(0, 0, 0), position.subtract(new Position(2, 4, 6)));
    }

    @Test
    void multiplyAndDivide() {
        Position position = new Position(1, 2, 3);
        assertEquals(new Position(2, 4, 6), position.multiply(2));
        assertEquals(new Position(0.5, 1, 1.5), position.divide(4));
    }

    @Test
    void setCoordinates() {
        Position position = new Position(1, 2, 3);
        position.setX(4);
        position.setY(5);
        position.setZ(6);
        assertEquals(new Position(4, 5, 6), position);
    }

    @Test
    void isZero() {
        Position position = new Position(0, 0, 0);
        assertTrue(position.isZero());
        position = new Position(1, 2, 3);
        assertFalse(position.isZero());
    }

    @Test
    void negate() {
        Position position = new Position(1, 2, 3);
        assertEquals(new Position(-1, -2, -3), position.negate());
    }

    @Test
    void dot() {
        Position position = new Position(1, 2, 3);
        assertEquals(32, position.dot(new Position(4, 5, 6)));
    }

    @Test
    void crossProduct() {
        Position position = new Position(1, 2, 3);
        Position other = new Position(4, 5, 6);
        assertEquals(new Position(-3, 6, -3), position.crossProduct(other));
    }

    @Test
    void length() {
        Position position = new Position(1, 2, 3);
        assertEquals(Math.sqrt(14), position.length());
    }

    @Test
    void normalize() {
        Position position = new Position(18, 12, 6);
        assertEquals(new Position(0.8017837257372732, 0.5345224838248488, 0.2672612419124244), position.normalize());
    }

    @Test
    void normalizeZero() {
        Position position = new Position(0, 0, 0);
        assertEquals(position, position.normalize());
    }

    @Test
    void distanceAndDistanceSquared() {
        Position position = new Position(1, 2, 3);
        assertEquals(Math.sqrt(27), position.distance(new Position(4, 5, 6)));
        assertEquals(27, position.distanceSquared(new Position(4, 5, 6)));
    }

    @Test
    void abs() {
        Position position = new Position(-1, -2, -3);
        assertEquals(new Position(1, 2, 3), position.abs());

        position = new Position(1, -2, 3);
        assertEquals(new Position(1, 2, 3), position.abs());
    }

    @Test
    void floorCeilRoundAndRoundPlace() {
        Position position = new Position(1.6, 2.5, 3.5);
        assertEquals(new Position(1, 2, 3), position.floor());
        position = new Position(1.6, 2.5, 3.5);
        assertEquals(new Position(2, 3, 4), position.ceil());
        position = new Position(1.6, 2.5, 3.5);
        assertEquals(new Position(2, 3, 4), position.round());
        position = new Position(1.6, 2.5, 3.5);
        assertEquals(new Position(2, 3, 4), position.round(0));

        position = new Position(1.47, 2.42, 3.43);
        assertEquals(new Position(1, 2, 3), position.floor());
        position = new Position(1.47, 2.42, 3.43);
        assertEquals(new Position(2, 3, 4), position.ceil());
        position = new Position(1.47, 2.42, 3.43);
        assertEquals(new Position(1, 2, 3), position.round());
        position = new Position(1.47, 2.42, 3.43);
        assertEquals(new Position(1.5, 2.4, 3.4), position.round(1));
    }

    @Test
    void testLookAt() {
        Position position = new Position(1, 2, 3);
        Rotation rotation = position.lookAt(new Position(position));
        assertEquals(0, rotation.getYaw());
        assertEquals(0, rotation.getPitch());
    }

    @Test
    void immutableTest() {
        Position position = new ImmutablePosition(1, 2, 3);
        assertThrows(UnsupportedOperationException.class, () -> position.setX(0));
        assertThrows(UnsupportedOperationException.class, () -> position.setY(0));
        assertThrows(UnsupportedOperationException.class, () -> position.setZ(0));

        assertThrows(UnsupportedOperationException.class, () -> position.add(new Position(0, 0, 0)));
        assertThrows(UnsupportedOperationException.class, () -> position.subtract(new Position(0, 0, 0)));

        assertThrows(UnsupportedOperationException.class, position::normalize);

        assertThrows(UnsupportedOperationException.class, () -> position.multiply(54));
        assertThrows(UnsupportedOperationException.class, () -> position.divide(2));

        assertThrows(UnsupportedOperationException.class, position::floor);
        assertThrows(UnsupportedOperationException.class, position::ceil);
        assertThrows(UnsupportedOperationException.class, position::round);
        assertThrows(UnsupportedOperationException.class, () -> position.round(0));

        assertThrows(UnsupportedOperationException.class, position::abs);
        assertThrows(UnsupportedOperationException.class, position::negate);

        assertEquals("1.0 2.0 3.0", new ImmutablePosition(position).toString());
        assertEquals(Position.class, position.copy().getClass());
    }

    @Test
    void toStringTest() {
        Position position = new Position(1, 2, 3);
        assertEquals("1.0 2.0 3.0", position.toString());
    }

    @Test
    void hashCodeTest() {
        Position position = new Position(1, 2, 3);
        assertEquals(position.hashCode(), new Position(1, 2, 3).hashCode());
    }

}
