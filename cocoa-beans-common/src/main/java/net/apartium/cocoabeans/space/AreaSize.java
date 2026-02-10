package net.apartium.cocoabeans.space;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents the size of a 3D area with width, height, and depth.
 * <p>
 * Provides utility methods for creating cubic areas, flooring dimensions, converting to regions,
 * and calculating the volume as an integer.
 * </p>
 * @param width the width of the area
 * @param height the height of the area
 * @param depth the depth of the area
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public record AreaSize(double width, double height, double depth) {

    /**
     * Constructs a new AreaSize by copying another AreaSize.
     * @param size the AreaSize to copy
     */
    public AreaSize(AreaSize size) {
        this(size.width, size.height, size.depth);
    }

    /**
     * Creates a cubic AreaSize with all sides equal to the given length.
     * @param length the length of each side
     * @return a new AreaSize with equal width, height, and depth
     */
    public static AreaSize box(double length) {
        return new AreaSize(length, length, length);
    }

    /**
     * Returns a new AreaSize with each dimension floored to the nearest integer.
     * @return a new AreaSize with floored dimensions
     */
    public AreaSize floor() {
        return new AreaSize(Math.floor(width), Math.floor(height), Math.floor(depth));
    }

    /**
     * Converts this AreaSize to a box-shaped region starting at the given position.
     * @param start the starting position
     * @return a StrictBoxRegion representing the area
     */
    public Region toBoxRegion(Position start) {
        return new StrictBoxRegion(
                start,
                new Position(start.getX() + width, start.getY() + height, start.getZ() + depth)
        );
    }

    /**
     * Calculates the volume of the area as an integer, flooring each dimension first.
     * @return the volume as an integer
     */
    public int toAreaSize() {
        return (int) Math.floor(width * height * depth);
    }
}
