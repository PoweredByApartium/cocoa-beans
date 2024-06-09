package net.apartium.cocoabeans.structs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoxRegionTest {

    @Test
    public void collide() {
        BoxRegion region = new BoxRegion(
                0, 3,
                0, 3,
                0, 3
        );

        assertTrue(region.contains(new Position(0, 0, 0)));
        assertTrue(region.contains(new Position(3, 3, 3)));
        assertTrue(region.contains(new Position(1, 1, 1)));
        assertTrue(region.contains(new Position(2, 2, 2)));

        assertFalse(region.contains(new Position(2, 2, 3.1)));
        assertFalse(region.contains(new Position(-5, 2, 3.1)));
        assertFalse(region.contains(new Position(2, 2, -0.9)));
    }

    @Test
    public void distance() {
        BoxRegion region = new BoxRegion(
                new Position(0, 0, 0),
                new Position(3, 3, 3)
        );

        assertEquals(0, region.distance(new Position(0, 0, 0)));
        assertEquals(0, region.distance(new Position(3, 3, 3)));
        assertEquals(0, region.distance(new Position(1, 1, 1)));
        assertEquals(0, region.distance(new Position(2, 2, 2)));

        assertTrue(Math.abs(region.distance(new Position(2, 2, 3.1)) - Math.sqrt(0.01)) < 0.0001);
        assertTrue(Math.abs(region.distance(new Position(-5, 2, 1)) - Math.sqrt(25)) < 0.0001);
    }

    @Test
    public void getAndSet() {
        BoxRegion region = new BoxRegion(
                new Position(0, 0, 0),
                new Position(3, 3, 3)
        );
        assertEquals(0, region.getX0());
        assertEquals(3, region.getX1());
        assertEquals(0, region.getY0());
        assertEquals(3, region.getY1());
        assertEquals(0, region.getZ0());
        assertEquals(3, region.getZ1());

        region.setX(4, 2);
        assertEquals(2, region.getX0());
        assertEquals(4, region.getX1());

        region.setY(4, 2);
        assertEquals(2, region.getY0());
        assertEquals(4, region.getY1());

        region.setZ(4, 2);
        assertEquals(2, region.getZ0());
        assertEquals(4, region.getZ1());

        region.set(new Position(-5, 6, 0), new Position(4, 2, 3));
        assertEquals(-5, region.getX0());
        assertEquals(4, region.getX1());
        assertEquals(2, region.getY0());
        assertEquals(6, region.getY1());
        assertEquals(0, region.getZ0());
        assertEquals(3, region.getZ1());
    }

}
