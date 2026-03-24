package net.apartium.cocoabeans.space.iterator;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class CubeShellIterator implements Iterator<Position> {

    private static final int FACE_COUNT = 6;

    private final AxisOrder axisOrder;
    private final int min;
    private final int max;

    private int face;

    private int a;
    private int b;

    private Position next;

    public CubeShellIterator(int radius) {
        this(-radius, radius);
    }

    public CubeShellIterator(int min, int max) {
        this(AxisOrder.XYZ, min, max);
    }

    public CubeShellIterator(AxisOrder axisOrder, int min, int max) {
        Ensures.largerThan(max, min, "max must be larger than or equal min");
        Ensures.notNull(axisOrder, "axisOrder");

        this.axisOrder = axisOrder;
        this.min = min;
        this.max = max;

        resetForFace(0);
        advance();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Position next() {
        if (next == null)
            throw new NoSuchElementException("No more elements");


        Position current = next;
        advance();
        return current;
    }

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

    private Position currentPosition() {
        return switch (face) {
            case 0 -> axisOrder.position(min, a, b);
            case 1 -> axisOrder.position(max, a, b);
            case 2 -> axisOrder.position(a, min, b);
            case 3 -> axisOrder.position(a, max, b);
            case 4 -> axisOrder.position(a, b, min);
            case 5 -> axisOrder.position(a, b, max);
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

    private void step() {
        b++;

        if (b > bMax(face)) {
            a++;
            b = bMin(face);
        }

        if (a > aMax(face)) {
            face++;
            if (face < FACE_COUNT)
                resetForFace(face);
        }
    }

    private void resetForFace(int face) {
        a = aMin(face);
        b = bMin(face);
    }

    private int aMin(int face) {
        return switch (face) {
            case 0, 1 -> min;
            case 2, 3, 4, 5 -> min + 1;
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

    private int aMax(int face) {
        return switch (face) {
            case 0, 1 -> max;
            case 2, 3, 4, 5 -> max - 1;
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

    private int bMin(int face) {
        return switch (face) {
            case 0, 1, 2, 3 -> min;
            case 4, 5 -> min + 1;
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }

    private int bMax(int face) {
        return switch (face) {
            case 0, 1, 2, 3 -> max;
            case 4, 5 -> max - 1;
            default -> throw new IllegalStateException("Unexpected face: " + face);
        };
    }
}