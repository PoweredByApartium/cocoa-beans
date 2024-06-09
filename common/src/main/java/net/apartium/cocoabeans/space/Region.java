package net.apartium.cocoabeans.space;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a region between 2 positions
 * @see BoxRegion
 * @see SphereRegion
 */
@ApiStatus.AvailableSince("0.0.22")
public interface Region {

    /**
     * Create a box region between 2 positions
     * @param pos0 position 0
     * @param pos1 position 1
     * @return a new box region instance
     */
    static BoxRegion box(Position pos0, Position pos1) {
        return new BoxRegion(pos0, pos1);
    }

    /**
     * Create a sphere region with center and radius
     * @param center center of the sphere
     * @param radius radius of the sphere
     * @return a new sphere region instance
     */
    static SphereRegion sphere(Position center, double radius) {
        return new SphereRegion(center, radius);
    }

    /**
     * Check if the position is in the region
     * @param position position to check
     * @return true if the position is in the region or false otherwise
     */
    boolean contains(Position position);

    /**
     * Return the distance between region and position
     * If position is inside the region, return 0
     * (Note that this is not the same as the distance between the region and the center of the region)
     * (Note this method is more expensive than {@link #contains(Position)} method when you just want to check if the position is inside the region)
     * @param position position
     * @return distance between region and position (or 0 if position is inside the region)
     */
    double distance(Position position);


}
