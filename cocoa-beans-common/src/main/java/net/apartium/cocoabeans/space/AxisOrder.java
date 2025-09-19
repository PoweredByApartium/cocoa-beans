package net.apartium.cocoabeans.space;

import java.util.*;

import static net.apartium.cocoabeans.CollectionHelpers.range;

public enum AxisOrder {
    XYZ((byte) 0, Axis.X, Axis.Y, Axis.Z),
    XZY((byte) 1, Axis.X, Axis.Z, Axis.Y),
    YXZ((byte) 2, Axis.Y, Axis.X, Axis.Z),
    YZX((byte) 3, Axis.Y, Axis.Z, Axis.X),
    ZXY((byte) 4, Axis.Z, Axis.X, Axis.Y),
    ZYX((byte) 5, Axis.Z, Axis.Y, Axis.X);

    private static final AxisOrder[] byIds;

    static {
        byIds = AxisOrder.values();
        for (int i = 0; i <= 5; i++) {
            if (byIds[i].id != i)
                throw new RuntimeException("AxisOrder as been loaded incorrectly: " + Arrays.toString(byIds));
        }
    }

    public static AxisOrder byId(byte id) {
        if (id < 0 || id > byIds.length)
            throw new IndexOutOfBoundsException("Couldn't find AxisOrder for " + id + " because index is out of bounds!");

        return byIds[id];
    }

    private final byte id;
    private final List<Axis> axes;
    AxisOrder(byte id, Axis axis0, Axis axis1, Axis axis2) {
        this.id = id;
        this.axes = List.of(axis0, axis1, axis2);
    }

    public byte getId() {
        return id;
    }

    public List<Axis> getAxes() {
        return axes;
    }

    public Iterator<Position> iterator(Position pos0, Position pos1, double step) {
        if (step <= 0.0) throw new IllegalArgumentException("step must be larger than 0");

        List<List<Double>> ranges = new ArrayList<>(3);
        for (Axis axis : axes) {
            double start = axis.getAlong(pos0);
            double end = axis.getAlong(pos1);
            ranges.add(range(start, end, step));
        }

        final int idxX = axes.indexOf(Axis.X);
        final int idxY = axes.indexOf(Axis.Y);
        final int idxZ = axes.indexOf(Axis.Z);

        return new Iterator<>() {
            int i0 = 0, i1 = 0, i2 = 0;
            final List<Double> range0 = ranges.get(0);
            final List<Double> range1 = ranges.get(1);
            final List<Double> range2 = ranges.get(2);
            boolean hasNext = !range0.isEmpty() && !range1.isEmpty() && !range2.isEmpty();

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Position next() {
                if (!hasNext)
                    throw new NoSuchElementException();

                double[] ordered = { range0.get(i0), range1.get(i1), range2.get(i2) };
                double x = ordered[idxX];
                double y = ordered[idxY];
                double z = ordered[idxZ];

                i2++;
                if (i2 >= range2.size()) {
                    i2 = 0;
                    i1++;
                }

                if (i1 >= range1.size()) {
                    i1 = 0;
                    i0++;
                }

                if (i0 >= range0.size())
                    hasNext = false;

                return new Position(x, y, z);
            }
        };
    }


}
