package net.apartium.cocoabeans.space.schematic.axis;

import net.apartium.cocoabeans.space.Position;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static net.apartium.cocoabeans.CollectionHelpers.range;

public class AxisIterator implements Iterator<Position> {

    private final int idxX;
    private final int idxY;
    private final int idxZ;

    private int i0 = 0, i1 = 0, i2 = 0;
    private final List<List<Double>> ranges;
    private final List<Double> range0;
    private final List<Double> range1;
    private final List<Double> range2;
    private boolean hasNext;


    public AxisIterator(AxisOrder axisOrder, Position pos0, Position pos1, double step) {
        if (step <= 0.0)
            throw new IllegalArgumentException("step must be larger than 0");

        this.ranges = new ArrayList<>(3);
        List<Axis> axes = axisOrder.getAxes();
        for (Axis axis : axes) {
            double start = axis.getAlong(pos0);
            double end = axis.getAlong(pos1);
            ranges.add(range(start, end, step));
        }

        this.idxX = axes.indexOf(Axis.X);
        this.idxY = axes.indexOf(Axis.Y);
        this.idxZ = axes.indexOf(Axis.Z);

        range0 = ranges.get(0);
        range1 = ranges.get(1);
        range2 = ranges.get(2);

        this.hasNext = !range0.isEmpty() && !range1.isEmpty() && !range2.isEmpty();
    }


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
}
