package net.apartium.cocoabeans.space.axis;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AxisOrderTest {

    @Test
    void byIdMatchesEnum() {
        for (AxisOrder order : AxisOrder.values()) {
            assertEquals(order, AxisOrder.byId(order.getId()));
        }

        assertThrows(IndexOutOfBoundsException.class, () -> AxisOrder.byId((byte) -1));
        assertThrows(IndexOutOfBoundsException.class, () -> AxisOrder.byId((byte) 6));
    }

    @Test
    void axesAccessors() {
        AxisOrder order = AxisOrder.XZY;
        assertEquals(List.of(Axis.X, Axis.Z, Axis.Y), order.getAxes());
        assertEquals(Axis.X, order.getFirst());
        assertEquals(Axis.Z, order.getSecond());
        assertEquals(Axis.Y, order.getThird());
    }

    @Test
    void positionAndAreaSizeMapping() {
        AxisOrder order = AxisOrder.YZX;
        Position position = order.position(1, 2, 3);
        assertEquals(new Position(3, 1, 2), position);

        AreaSize size = order.areaSize(1, 2, 3);
        assertEquals(new AreaSize(3, 1, 2), size);

        order = AxisOrder.XYZ;
        position = order.position(1, 2, 3);
        assertEquals(new Position(1, 2, 3), position);
    }

    @Test
    void compareRespectsAxisOrder() {
        AxisOrder order = AxisOrder.XZY;
        Position a = new Position(1, 2, 3);
        Position b = new Position(1, 3, 2);
        assertTrue(order.compare(a, b) > 0);

        Position c = new Position(1, 2, 3);
        Position d = new Position(1, 3, 3);
        assertTrue(order.compare(c, d) < 0);
    }
}
