package net.apartium.cocoabeans.space.iterator;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over all positions on the outer shell of a cube defined by {@code [min, max]}.
 *
 * <p>The iterator visits all positions where at least one coordinate is equal to
 * {@code min} or {@code max}, effectively skipping all interior positions.</p>
 *
 * <p>Iteration is performed face-by-face (X, Y, Z), while avoiding duplicates
 * between faces.</p>
 *
 * <p>The {@code u} and {@code v} fields represent the two varying coordinates
 * on the current face, while the third coordinate is fixed.</p>
 *
 * <p>If {@code min == max}, a single position is returned.</p>
 */
@ApiStatus.AvailableSince("0.0.49")
public final class CubeShellIterator implements Iterator<Position> {

    private static final int FACE_COUNT = 6;

    private final AxisOrder axisOrder;
    private final int min;
    private final int max;

    private int face;

    private int u;
    private int v;

    private Position next;

    /**
     * Creates an iterator over a cube shell centered at 0 with the given radius.
     *
     * @param radius the cube radius (range will be {@code [-radius, radius]})
     */
    public CubeShellIterator(int radius) {
        this(-radius, radius);
    }

    /**
     * Creates an iterator over a cube shell in the range {@code [min, max]}
     * using the default {@link AxisOrder#XYZ}.
     *
     * @param min the minimum coordinate (inclusive)
     * @param max the maximum coordinate (inclusive)
     */
    public CubeShellIterator(int min, int max) {
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
    public CubeShellIterator(AxisOrder axisOrder, int min, int max) {
        Ensures.largerThan(max, min, "max must be larger than or equal min");
        Ensures.notNull(axisOrder, "axisOrder");

        this.axisOrder = axisOrder;
        this.min = min;
        this.max = max;

        resetFace();
        advance();
    }

    /**
     * Returns whether there are more positions in the shell.
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }

    /**
     * Returns the next position in the shell.
     *
     * @throws NoSuchElementException if no more positions exist
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
     * Advances the iterator to the next position.
     */
    private void advance() {
        if (face >= FACE_COUNT) {
            next = null;
            return;
        }

        if (min == max) {
            next = axisOrder.position(min, min, min);
            face = FACE_COUNT;
            return;
        }

        next = currentPosition();
        step();
    }

    /**
     * Advances the iterator to the next position.
     */
    private Position currentPosition() {
        return switch (face) {
            case 0 -> axisOrder.position(min, u, v);
            case 1 -> axisOrder.position(max, u, v);
            case 2 -> axisOrder.position(u, min, v);
            case 3 -> axisOrder.position(u, max, v);
            case 4 -> axisOrder.position(u, v, min);
            case 5 -> axisOrder.position(u, v, max);
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

    /**
     * Moves to the next coordinate within the current face.
     * Advances to the next face when finished.
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
     * Resets {@code u} and {@code v} for the current face.
     */
    private void resetFace() {
        u = minU();
        v = minV();
    }

    private int minU() {
        return face < 2 ? min : min + 1;
    }

    private int maxU() {
        return face < 2 ? max : max - 1;
    }

    private int minV() {
        return face < 4 ? min : min + 1;
    }

    private int maxV() {
        return face < 4 ? max : max - 1;
    }
}