package net.apartium.cocoabeans.space.iterator;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over all positions on the surface of an axis-aligned box.
 *
 * <p>A position is included if at least one of its coordinates is equal to the
 * minimum or maximum bound of the box. Interior positions are skipped.</p>
 *
 * <p>Positions are emitted face by face. The iterator adjusts the coordinate
 * ranges per face so edges and corners are not returned more than once.</p>
 *
 * <p>If {@code min} and {@code max} describe the same position, the iterator
 * returns that single position.</p>
 */
@ApiStatus.AvailableSince("0.0.49")
public final class BoxSurfaceIterator implements Iterator<Position> {

    private static final int FACE_COUNT = 6;

    private final AxisOrder axisOrder;
    private final Position min;
    private final Position max;

    private int face;

    /**
     * Current U coordinate on the face (horizontal texture axis).
     */
    private int u;

    /**
     * Current V coordinate on the face (vertical texture axis).
     */
    private int v;

    private Position next;

    /**
     * Creates an iterator over a cube shell centered at 0 with the given radius.
     *
     * @param radius the cube radius (range will be {@code [-radius, radius]})
     */
    public BoxSurfaceIterator(int radius) {
        this(-radius, radius);
    }

    /**
     * Creates an iterator over a cube shell in the range {@code [min, max]}
     * using the default {@link AxisOrder#XYZ}.
     *
     * @param min the minimum coordinate (inclusive)
     * @param max the maximum coordinate (inclusive)
     */
    public BoxSurfaceIterator(int min, int max) {
        this(AxisOrder.XYZ, min, max);
    }

    /**
     * Creates an iterator over a cube shell in the range {@code [min, max]}.
     *
     * @param axisOrder the axis order used to construct positions
     * @param min the minimum coordinate (inclusive)
     * @param max the maximum coordinate (inclusive)
     *
     * @throws IllegalArgumentException if {@code max < min}
     * @throws NullPointerException if {@code axisOrder} is null
     */
    public BoxSurfaceIterator(AxisOrder axisOrder, int min, int max) {
        this(
                axisOrder,
                new Position(min, min, min),
                new Position(max, max, max)
        );
    }

    /**
     * Creates an iterator over the surface of a rectangular box.
     *
     * <p>The given bounds are normalized with {@link Position#floor()} and then
     * stored as immutable positions.</p>
     *
     * @param axisOrder the axis order used when constructing returned positions
     * @param min the minimum corner, inclusive
     * @param max the maximum corner, inclusive
     *
     * @throws IllegalArgumentException if any coordinate in {@code max} is
     *         smaller than the corresponding coordinate in {@code min}
     * @throws NullPointerException if {@code axisOrder}, {@code min}, or
     *         {@code max} is {@code null}
     */
    public BoxSurfaceIterator(AxisOrder axisOrder, Position min, Position max) {
        min = new Position(min).floor().immutable();
        max = new Position(max).floor().immutable();

        Ensures.largerThan((int) max.getX(), (int) min.getX(), "max#getX must be larger than or equals min#getX");
        Ensures.largerThan((int) max.getY(), (int) min.getY(), "max#getY must be larger than or equals min#getY");
        Ensures.largerThan((int) max.getZ(), (int) min.getZ(), "max#getZ must be larger than or equals min#getZ");

        Ensures.notNull(axisOrder, "axisOrder");

        this.axisOrder = axisOrder;
        this.min = min;
        this.max = max;

        resetFace();
        advance();
    }

    /**
     * Returns whether another surface position is available.
     *
     * @return {@code true} if the iterator has more positions
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }

    /**
     * Returns the next surface position.
     *
     * @return the next position on the box surface
     * @throws NoSuchElementException if no more positions are available
     */
    @Override
    public Position next() {
        if (next == null)
            throw new NoSuchElementException("No more elements");


        Position current = next;
        advance();
        return current;
    }

    /**
     * Returns the next surface position without advancing the iterator.
     * @return the next position on the box surface
     */
    public Position peek() {
        if (next == null)
            throw new NoSuchElementException("No more elements");

        return new Position(next);
    }

    /**
     * Prepares the next position to return.
     *
     * <p>If all faces were exhausted, the iterator becomes empty.</p>
     */
    private void advance() {
        if (face >= FACE_COUNT) {
            next = null;
            return;
        }

        if (min.equals(max)) {
            next = new Position(min);
            face = FACE_COUNT;
            return;
        }

        next = currentPosition();
        step();
    }

    /**
     * Creates the current position for the active face.
     *
     * @return the current surface position
     * @throws IllegalStateException if the current face index is invalid
     */
    private Position currentPosition() {
        return switch (face) {
            case 0 -> axisOrder.position((int) axisOrder.getFirst().getAlong(min), u, v);
            case 1 -> axisOrder.position((int) axisOrder.getFirst().getAlong(max), u, v);
            case 2 -> axisOrder.position(u, (int) axisOrder.getSecond().getAlong(min), v);
            case 3 -> axisOrder.position(u, (int) axisOrder.getSecond().getAlong(max), v);
            case 4 -> axisOrder.position(u, v, (int) axisOrder.getThird().getAlong(min));
            case 5 -> axisOrder.position(u, v, (int) axisOrder.getThird().getAlong(max));
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

    /**
     * Moves to the next coordinate on the current face.
     *
     * <p>When the current face is exhausted, iteration continues on the next
     * face.</p>
     */
    private void step() {
        v++;

        if (v > maxV()) {
            v = minV();
            u++;
        }

        if (u > maxU()) {
            face++;
            if (face < FACE_COUNT)
                resetFace();
        }
    }

    /**
     * Resets the current face coordinates to their starting values.
     */
    private void resetFace() {
        u = minU();
        v = minV();
    }

    /**
     * Returns the minimum {@code u} value for the current face.
     *
     * <p>For non-primary faces, the lower bound is shifted inward to avoid
     * returning duplicate edge positions.</p>
     *
     * @return the minimum {@code u} coordinate for the active face
     */
    private int minU() {
        int minValue = (int) getAxisFaceU().getAlong(min);
        return face < 2 ? minValue : minValue + 1;
    }

    /**
     * Returns the maximum {@code u} value for the current face.
     *
     * <p>For non-primary faces, the upper bound is shifted inward to avoid
     * returning duplicate edge positions.</p>
     *
     * @return the maximum {@code u} coordinate for the active face
     */
    private int maxU() {
        int maxValue = (int) getAxisFaceU().getAlong(max);
        return face < 2 ? maxValue : maxValue - 1;
    }

    /**
     * Returns the minimum {@code v} value for the current face.
     *
     * <p>For later faces, the lower bound is shifted inward to avoid returning
     * duplicate edge positions.</p>
     *
     * @return the minimum {@code v} coordinate for the active face
     */
    private int minV() {
        int minValue = (int) getAxisFaceV().getAlong(min);
        return face < 4 ? minValue : minValue + 1;
    }

    /**
     * Returns the maximum {@code v} value for the current face.
     *
     * <p>For later faces, the upper bound is shifted inward to avoid returning
     * duplicate edge positions.</p>
     *
     * @return the maximum {@code v} coordinate for the active face
     */
    private int maxV() {
        int maxValue = (int) getAxisFaceV().getAlong(max);
        return face < 4 ? maxValue : maxValue - 1;
    }


    /**
     * Returns the axis used as the {@code u} coordinate for the current face.
     *
     * @return the axis mapped to {@code u}
     * @throws IllegalStateException if the current face index is invalid
     */
    private Axis getAxisFaceU() {
        return switch(face) {
            case 0, 1 -> axisOrder.getSecond();
            case 2, 3, 4, 5 -> axisOrder.getFirst();
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

    /**
     * Returns the axis used as the {@code v} coordinate for the current face.
     *
     * @return the axis mapped to {@code v}
     * @throws IllegalStateException if the current face index is invalid
     */
    private Axis getAxisFaceV() {
        return switch(face) {
            case 0, 1, 2, 3 -> axisOrder.getThird();
            case 4, 5 -> axisOrder.getSecond();
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

}