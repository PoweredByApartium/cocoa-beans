/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Rotation;
import net.apartium.cocoabeans.space.Transform;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Bukkit locations systems helper methods
 */
public class Locations {

    /**
     * Checking if the locations is in the same world
     *
     * @param loc1
     *            the first location
     * @param loc2
     *            the second location
     * @return true if those locations are in the same world, else false
     */
    public static boolean isSameWorld(Location loc1, Location loc2) {
        Ensures.notNull(loc1, "loc1 +-");
        Ensures.notNull(loc2, "loc2 +-");

        return loc1.getWorld().equals(loc2.getWorld());
    }

    /**
     * Get all the locations between two positions
     *
     * @param pos1 the first position
     * @param pos2 the second position
     * @return the locations between those positions
     */
    public static Set<Location> getLocationsBetween(Location pos1, Location pos2) {
        Ensures.isTrue(isSameWorld(pos1, pos2), "pos1 and pos2 must be in the same world");

        Set<Location> set = new HashSet<>();

        int maxX, minX, maxY, minY, maxZ, minZ;
        World world = pos1.getWorld();

        if (pos1.getBlockX() > pos2.getBlockX()) {
            maxX = pos1.getBlockX();
            minX = pos2.getBlockX();
        } else {
            maxX = pos2.getBlockX();
            minX = pos1.getBlockX();
        }

        if (pos1.getBlockZ() > pos2.getBlockZ()) {
            maxZ = pos1.getBlockZ();
            minZ = pos2.getBlockZ();
        } else {
            maxZ = pos2.getBlockZ();
            minZ = pos1.getBlockZ();
        }

        if (pos1.getBlockY() > pos2.getBlockY()) {
            maxY = pos1.getBlockY();
            minY = pos2.getBlockY();
        } else {
            maxY = pos2.getBlockY();
            minY = pos1.getBlockY();
        }

        for (int x = minX; x < maxX; x++)
            for (int y = minY; y < maxY; y++)
                for (int z = minZ; z < maxZ; z++)
                    set.add(new Location(world, x, y, z));

        return set;
    }

    /**
     * Convert a location to a position
     * @param location the location
     * @return the position
     */
    @ApiStatus.AvailableSince("0.0.22")
    public static Position toPosition(Location location) {
        return new Position(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Convert a location to rotation
     * @param location the location
     * @return the rotation
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static Rotation toRotation(Location location) {
        return new Rotation(location.getYaw(), location.getPitch());
    }

    /**
     * Convert a location to rotation
     * @param location the location
     * @return the transform
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static Transform toTransform(Location location) {
        return new Transform(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Convert a position to a location
     * @param world bukkit world
     * @param position cocoa beans position
     * @see Position
     * @return the location
     */
    @ApiStatus.AvailableSince("0.0.22")
    public static Location toLocation(World world, Position position) {
        return new Location(world, position.getX(), position.getY(), position.getZ());
    }

    /**
     * Convert a transform to a location
     * @param world bukkit world
     * @param transform cocoa beans transform
     * @see Transform
     * @return the location
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static Location toLocation(World world, Transform transform) {
        return new Location(world, transform.getX(), transform.getY(), transform.getZ(), transform.getYaw(), transform.getPitch());
    }
}
