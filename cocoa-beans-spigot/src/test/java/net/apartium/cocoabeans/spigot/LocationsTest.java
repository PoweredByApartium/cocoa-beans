package net.apartium.cocoabeans.spigot;

import be.seeseemelk.mockbukkit.WorldMock;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Rotation;
import net.apartium.cocoabeans.space.Transform;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LocationsTest extends CocoaBeansTestBase {

    private Location location, copy, diffLocation;
    private WorldMock world;

    @BeforeEach
    public void setup() {
        super.setup();
        world = new WorldMock();
        WorldMock diffWorld = new WorldMock();
        this.server.addWorld(world);
        this.server.addWorld(diffWorld);

        this.location = new Location(world, 1, 2, 3, 4, 5);
        this.copy = new Location(world, 1, 2, 3, 4 ,5);
        this.diffLocation = new Location(diffWorld, 4, 5, 6, 7, 8);
    }

    @Test
    void testIsSameWorld() {
        assertTrue(Locations.isSameWorld(this.location, this.copy), "Test locations with same worlds");
        assertFalse(Locations.isSameWorld(this.location, this.diffLocation), "Test locations with different worlds");
    }

    @Test
    void testGetLocationsBetween() {
        assertThrows(NullPointerException.class, () ->
            Locations.getLocationsBetween(location, diffLocation)
        );

        Location location0 = new Location(this.world, 0, 0, 0);
        Location location1 = new Location(this.world, 0, 0, 0);

        Set<Location> sameResult = Locations.getLocationsBetween(location1, location0);
        assertTrue(sameResult.isEmpty());

        Location location3 = new Location(world, -1, -1, -1);
        Location location4 = new Location(world, 1, 1, 1);

        Set<Location> negativeResult = Locations.getLocationsBetween(location3, location4);

        assertEquals(8, negativeResult.size());
        assertTrue(negativeResult.contains(new Location(world, -1, -1, -1)));
        assertFalse(negativeResult.contains(new Location(world, 1, 1, 1)));

        Location location5 = new Location(world, 1, 1, 1);

        Set<Location> result = Locations.getLocationsBetween(location0, location5);

        assertEquals(1, result.size());
        assertTrue(result.contains(new Location(world, 0, 0, 0)));
    }

    @Test
    void testGetLocationsBetweenInverse() {
        Location location0 = new Location(this.world, 1, 1, 1);
        Location location1 = new Location(this.world, 0, 0, 0);

        Set<Location> sameResult = Locations.getLocationsBetween(location0, location1);
        assertFalse(sameResult.isEmpty());
    }


    @Test
    void testToPosition() {
        Position position = Locations.toPosition(location);
        assertEquals(1, position.getX());
        assertEquals(2, position.getY());
        assertEquals(3, position.getZ());
    }

    @Test
    void testToRotation() {
        Rotation rotation = Locations.toRotation(location);
        assertEquals(4, rotation.getYaw());
        assertEquals(5, rotation.getPitch());
    }

    @Test
    void testToTransform() {
        Transform transform = Locations.toTransform(location);
        assertEquals(1, transform.getX());
        assertEquals(2, transform.getY());
        assertEquals(3, transform.getZ());
        assertEquals(4, transform.getYaw());
        assertEquals(5, transform.getPitch());
    }

    @Test
    void testToLocation() {
        Position position = Locations.toPosition(location);
        Location positionLocation = Locations.toLocation(this.world, position);
        assertEquals(positionLocation.getWorld(), this.world, "Position -> Location test world");
        assertEquals(positionLocation.getX(), position.getX(), "Position -> Location test x");
        assertEquals(positionLocation.getY(), position.getY(), "Position -> Location test y");
        assertEquals(positionLocation.getZ(), position.getZ(),"Position -> Location test z");

        Transform transform = Locations.toTransform(location);
        Location transformLocation = Locations.toLocation(this.world, transform);
        assertEquals(transformLocation.getWorld(), this.world, "Transform -> Location test world");
        assertEquals(transformLocation.getX(), transform.getX(), "Transform -> Location test x");
        assertEquals(transformLocation.getY(), transform.getY(), "Transform -> Location test y");
        assertEquals(transformLocation.getZ(), transform.getZ(), "Transform -> Location test z");
        assertEquals(transformLocation.getYaw(), transform.getYaw(), "Transform -> Location test yaw");
        assertEquals(transformLocation.getPitch(), transform.getPitch(), "Transform -> Location test pitch");

        Vector positionVector = Locations.toVector(position);
        assertEquals(positionVector.getX(), position.getX(), "Position -> Vector test x");
        assertEquals(positionVector.getY(), position.getY(), "Position -> Vector test y");
        assertEquals(positionVector.getZ(), position.getZ(),"Position -> Vector test z");

        Vector transformVector = Locations.toVector(transform);
        assertEquals(transformVector.getX(), transform.getX(), "Transform -> Vector test x");
        assertEquals(transformVector.getY(), transform.getY(), "Transform -> Vector test y");
        assertEquals(transformVector.getZ(), transform.getZ(),"Transform -> Vector test z");
    }
}
