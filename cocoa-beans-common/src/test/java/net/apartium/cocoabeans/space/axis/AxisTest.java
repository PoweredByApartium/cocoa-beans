package net.apartium.cocoabeans.space.axis;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AxisTest {

    @Test
    void getAlongPosition() {
        Position position = new Position(1, 2, 3);
        assertEquals(1, Axis.X.getAlong(position));
        assertEquals(2, Axis.Y.getAlong(position));
        assertEquals(3, Axis.Z.getAlong(position));
    }

    @Test
    void getAlongAreaSize() {
        AreaSize size = new AreaSize(4, 5, 6);
        assertEquals(4, Axis.X.getAlong(size));
        assertEquals(5, Axis.Y.getAlong(size));
        assertEquals(6, Axis.Z.getAlong(size));
    }
}
