package net.apartium.cocoabeans.space.axis;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Represents the order of axes in 3D space and provides utilities for working with positions and dimensions
 * according to the specified axis order. Implements {@link Comparator} for comparing {@link Position} objects
 * based on the axis order.
 * <p>
 * Each enum constant defines a unique permutation of the axes (X, Y, Z).
 * </p>
 * <ul>
 *   <li>XYZ: X, then Y, then Z</li>
 *   <li>XZY: X, then Z, then Y</li>
 *   <li>YXZ: Y, then X, then Z</li>
 *   <li>YZX: Y, then Z, then X</li>
 *   <li>ZXY: Z, then X, then Y</li>
 *   <li>ZYX: Z, then Y, then X</li>
 * </ul>
 * <p>
 * Provides methods for retrieving axes, creating positions and dimensions, iterating over positions,
 * and comparing positions according to the axis order.
 * </p>
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public enum AxisOrder implements Comparator<Position> {
    /** X, then Y, then Z */
    XYZ((byte) 0, Axis.X, Axis.Y, Axis.Z),
    /** X, then Z, then Y */
    XZY((byte) 1, Axis.X, Axis.Z, Axis.Y),
    /** Y, then X, then Z */
    YXZ((byte) 2, Axis.Y, Axis.X, Axis.Z),
    /** Y, then Z, then X */
    YZX((byte) 3, Axis.Y, Axis.Z, Axis.X),
    /** Z, then X, then Y */
    ZXY((byte) 4, Axis.Z, Axis.X, Axis.Y),
    /** Z, then Y, then X */
    ZYX((byte) 5, Axis.Z, Axis.Y, Axis.X);

    /**
     * Array of AxisOrder constants indexed by their id.
     */
    private static final AxisOrder[] byIds;

    static {
        byIds = AxisOrder.values();
        for (int i = 0; i <= 5; i++) {
            if (byIds[i].id != i)
                throw new RuntimeException("AxisOrder as been loaded incorrectly: " + Arrays.toString(byIds));
        }
    }

    /**
     * Returns the AxisOrder constant by its id.
     * @param id the id of the AxisOrder
     * @return the AxisOrder for the given id
     * @throws IndexOutOfBoundsException if the id is out of bounds
     */
    public static AxisOrder byId(byte id) {
        if (id < 0 || id > byIds.length)
            throw new IndexOutOfBoundsException("Couldn't find AxisOrder for " + id + " because index is out of bounds!");

        return byIds[id];
    }

    private final byte id;
    private final List<Axis> axes;

    /**
     * Constructs an AxisOrder with the specified id and axes.
     * @param id the id of the AxisOrder
     * @param axis0 the first axis
     * @param axis1 the second axis
     * @param axis2 the third axis
     */
    AxisOrder(byte id, Axis axis0, Axis axis1, Axis axis2) {
        this.id = id;
        this.axes = List.of(axis0, axis1, axis2);
    }

    /**
     * Returns the id of this AxisOrder.
     * @return the id
     */
    public byte getId() {
        return id;
    }

    /**
     * Returns the list of axes in this order.
     * @return the axes
     */
    public List<Axis> getAxes() {
        return axes;
    }

    /**
     * Returns an iterator over positions between pos0 and pos1 with the given step, following this axis order.
     * @param pos0 the starting position
     * @param pos1 the ending position
     * @param step the step size
     * @return an iterator over positions
     */
    public Iterator<Position> iterator(Position pos0, Position pos1, double step) {
        return new AxisIterator(this, pos0, pos1, step);
    }

    /**
     * Returns the first axis in this order.
     * @return the first axis
     */
    public Axis getFirst() {
        return axes.get(0);
    }

    /**
     * Returns the second axis in this order.
     * @return the second axis
     */
    public Axis getSecond() {
        return axes.get(1);
    }

    /**
     * Returns the third axis in this order.
     * @return the third axis
     */
    public Axis getThird() {
        return axes.get(2);
    }

    /**
     * Creates a Position object using the specified values for each axis according to this order.
     * @param i0 value for the first axis
     * @param i1 value for the second axis
     * @param i2 value for the third axis
     * @return a Position object
     */
    public Position position(int i0, int i1, int i2) {
        Axis first = getFirst();
        Axis second = getSecond();
        Axis third = getThird();
        return new Position(
                first == Axis.X ? i0 : first == Axis.Y ? i1 : i2,
                second == Axis.X ? i0 : second == Axis.Y ? i1 : i2,
                third == Axis.X ? i0 : third == Axis.Y ? i1 : i2
        );
    }

    /**
     * Creates an AreaSize object using the specified values for each axis according to this order.
     * @param i0 value for the first axis
     * @param i1 value for the second axis
     * @param i2 value for the third axis
     * @return an AreaSize object
     */
    public AreaSize dimensions(int i0, int i1, int i2) {
        Axis first = getFirst();
        Axis second = getSecond();
        Axis third = getThird();
        return new AreaSize(
                first == Axis.X ? i0 : first == Axis.Y ? i1 : i2,
                second == Axis.X ? i0 : second == Axis.Y ? i1 : i2,
                third == Axis.X ? i0 : third == Axis.Y ? i1 : i2
        );
    }

    /**
     * Compares two positions according to this axis order.
     * @param position the first position
     * @param other the second position
     * @return a negative integer, zero, or a positive integer as the first position is less than, equal to, or greater than the second
     */
    @Override
    public int compare(Position position, Position other) {
        Axis axis0 = getAxes().get(0);
        Axis axis1 = getAxes().get(1);
        Axis axis2 = getAxes().get(2);

        int compare = Double.compare(axis0.getAlong(position), axis0.getAlong(other));
        if (compare != 0)
            return compare;

        compare = Double.compare(axis1.getAlong(position), axis1.getAlong(other));
        if (compare != 0)
            return compare;

        return Double.compare(axis2.getAlong(position), axis2.getAlong(other));
    }
}
