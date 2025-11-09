package net.apartium.cocoabeans.space.axis;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.AvailableSince("0.0.46")
public enum AxisOrder implements Comparator<Position> {
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
        return new AxisIterator(this, pos0, pos1, step);
    }

    public Axis getFirst() {
        return axes.get(0);
    }

    public Axis getSecond() {
        return axes.get(1);
    }

    public Axis getThird() {
        return axes.get(2);
    }

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
