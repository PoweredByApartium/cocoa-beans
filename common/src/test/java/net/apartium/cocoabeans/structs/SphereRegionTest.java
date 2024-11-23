package net.apartium.cocoabeans.structs;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Region;
import net.apartium.cocoabeans.space.SphereRegion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SphereRegionTest {

    @Test
    void sphere() {
        SphereRegion sphere = Region.sphere(
                Position.UP,
                5
        );
        assertNotNull(sphere);
    }

    @Test
    void collide() {
        SphereRegion region = new SphereRegion(
                new Position(0, 0, 0),
                5
        );

        assertTrue(region.contains(new Position(0, 0, 0)));
        assertTrue(region.contains(new Position(1, 0, 0)));
        assertTrue(region.contains(new Position(2, 0, 0)));
        assertTrue(region.contains(new Position(3, 0, 0)));
        assertTrue(region.contains(new Position(4, 0, 0)));
        assertTrue(region.contains(new Position(5, 0, 0)));

        assertFalse(region.contains(new Position(5, 1, 0)));
    }

    @Test
    void distance() {
        SphereRegion region = new SphereRegion(
                new Position(0, 0, 0),
                5
        );

        assertEquals(0, region.distance(new Position(3, 3.4, 2)));
        assertTrue(region.distance(new Position(8, 0, 4)) - Math.sqrt(55) < 0.0001);
    }

    @Test
    void getCenterAndSet() {
        SphereRegion region = new SphereRegion(
                new Position(0, 0, 0),
                5
        );
        assertEquals(new Position(0, 0, 0), region.getCenter());

        region.setCenter(new Position(5, 5, 5));
        assertEquals(new Position(5, 5, 5), region.getCenter());
    }

    @Test
    void getRadiusAndSet() {
        SphereRegion region = new SphereRegion(
                new Position(0, 0, 0),
                5
        );
        assertEquals(5, region.getRadius());

        region.setRadius(10);
        assertEquals(10, region.getRadius());
    }

}
