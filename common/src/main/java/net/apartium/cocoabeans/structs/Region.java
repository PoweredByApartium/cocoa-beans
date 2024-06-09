package net.apartium.cocoabeans.structs;

public interface Region {

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
