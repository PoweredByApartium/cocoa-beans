package net.apartium.cocoabeans.space.iterator;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.space.Position;

import java.util.Iterator;

public class CubeShellIterator implements Iterator<Position> {

    private final int min;
    private final int max;

    private int i0;
    private int i1;

    private int face = 0;

    private Position next = null;

    public CubeShellIterator(int radius) {
        this(-radius, radius);
    }

    public CubeShellIterator(int min, int max) {
        Ensures.largerThan(max, min, "max must be larger than or equal min");

        this.min = min;
        this.max = max;

        this.i0 = min;
        this.i1 = min;

        advance();
    }

    private void advance() {
        if (face < 0 || face >= 6) {
            next = null;
            return;
        }

        if (min == max) {
            face = 6;
            next = new Position(min, min, min);
            return;
        }

        next = switch (face) {
            case 0 -> advanceFace0();
            case 1 -> advanceFace1();
            case 2 -> advanceFace2();
            case 3 -> advanceFace3();
            case 4 -> advanceFace4();
            case 5 -> advanceFace5();
            default -> throw new IllegalStateException("Unexpected value: " + face);
        };
    }

    private Position advanceFace0() {
        Position result = new Position(min, i0, i1);
        i1++;

        if (i1 > max) {
            i0++;
            i1 = min;
        }

        if (i0 > max) {
            face = 1;
            i0 = min;
            i1 = min;
        }

        return result;
    }

    private Position advanceFace1() {
        Position result = new Position(max, i0, i1);
        i1++;

        if (i1 > max) {
            i0++;
            i1 = min;
        }

        if (i0 > max) {
            face = 2;
            i0 = min + 1;
            i1 = min;
        }

        return result;
    }

    private Position advanceFace2() {
        Position result = new Position(i0, min, i1);
        i1++;

        if (i1 > max) {
            i0++;
            i1 = min;
        }

        if (i0 > max - 1) {
            face = 3;
            i0 = min + 1;
            i1 = min;
        }

        return result;
    }

    private Position advanceFace3() {
        Position result = new Position(i0, max, i1);
        i1++;

        if (i1 > max) {
            i0++;
            i1 = min;
        }

        if (i0 > max - 1) {
            face = 4;
            i0 = min + 1;
            i1 = min + 1;
        }

        return result;
    }

    private Position advanceFace4() {
        Position result = new Position(i0, i1, min);
        i1++;

        if (i1 > max - 1) {
            i0++;
            i1 = min + 1;
        }

        if (i0 > max - 1) {
            face = 5;
            i0 = min + 1;
            i1 = min + 1;
        }

        return result;
    }

    private Position advanceFace5() {
        Position result = new Position(i0, i1, max);
        i1++;

        if (i1 > max - 1) {
            i0++;
            i1 = min + 1;
        }

        if (i0 > max - 1) {
            face = 6;
        }

        return result;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Position next() {
        if (next == null) {
            throw new IllegalStateException("No more elements");
        }

        Position result = next;
        advance();
        return result;
    }
}