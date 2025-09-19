package net.apartium.cocoabeans.space;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AxisOrderTest {

    @Test
    void iteratorXYZ() {
        AxisOrder order = AxisOrder.XYZ;

        Iterator<Position> iterator = order.iterator(
                new Position(0, 0, 0),
                new Position(5, 5, 5),
                1
        );

        for (double x = 0; x <= 5; x++) {
            for (double y = 0; y <= 5; y++) {
                for (double z = 0; z <= 5; z++) {
                    if (!iterator.hasNext())
                        fail("Doesn't have next!");

                    assertEquals(
                            new Position(x, y, z),
                            iterator.next()
                    );
                }
            }
        }

        iterator = order.iterator(
                new Position(2, 3, 7),
                new Position(11, -5, 18),
                1
        );

        for (double x = 2; x <= 11; x++) {
            for (double y = 3; y >= -5; y--) {
                for (double z = 7; z <= 18; z++) {
                    if (!iterator.hasNext())
                        fail("Doesn't have next: " + x + ", " + y + ", " + z);

                    assertEquals(
                            new Position(x, y, z),
                            iterator.next()
                    );
                }
            }
        }
    }

    @Test
    void iteratorYXZ() {
        AxisOrder order = AxisOrder.YXZ;

        Iterator<Position> iterator = order.iterator(
                new Position(0, 0, 0),
                new Position(5, 5, 5),
                1
        );

        for (double y = 0; y <= 5; y++) {
            for (double x = 0; x <= 5; x++) {
                for (double z = 0; z <= 5; z++) {
                    if (!iterator.hasNext())
                        fail("Doesn't have next!");

                    assertEquals(
                            new Position(x, y, z),
                            iterator.next()
                    );
                }
            }
        }

        iterator = order.iterator(
                new Position(2, 3, 7),
                new Position(11, -5, 18),
                1
        );

        for (double y = 3; y >= -5; y--) {
            for (double x = 2; x <= 11; x++) {
                for (double z = 7; z <= 18; z++) {
                    if (!iterator.hasNext())
                        fail("Doesn't have next: " + x + ", " + y + ", " + z);

                    assertEquals(
                            new Position(x, y, z),
                            iterator.next()
                    );
                }
            }
        }
    }

}
