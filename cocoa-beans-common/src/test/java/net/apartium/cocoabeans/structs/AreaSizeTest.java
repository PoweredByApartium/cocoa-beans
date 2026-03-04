package net.apartium.cocoabeans.structs;

import net.apartium.cocoabeans.space.AreaSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AreaSizeTest {

    @Test
    void constructor() {
        AreaSize size = new AreaSize(2.0, 3.0, 4.0);
        assertEquals(2.0, size.width());
        assertEquals(3.0, size.height());
        assertEquals(4.0, size.depth());
    }

    @Test
    void copyConstructor() {
        AreaSize original = new AreaSize(2.0, 3.0, 4.0);
        AreaSize copy = new AreaSize(original);
        assertEquals(original.width(), copy.width());
        assertEquals(original.height(), copy.height());
        assertEquals(original.depth(), copy.depth());
        assertEquals(original, copy);
    }

    @Test
    void box() {
        AreaSize size = AreaSize.box(5.0);
        assertEquals(5.0, size.width());
        assertEquals(5.0, size.height());
        assertEquals(5.0, size.depth());
    }

    @Test
    void boxWithFraction() {
        AreaSize size = AreaSize.box(2.5);
        assertEquals(2.5, size.width());
        assertEquals(2.5, size.height());
        assertEquals(2.5, size.depth());
    }

    @Test
    void floor() {
        AreaSize size = new AreaSize(2.9, 3.7, 4.1);
        AreaSize floored = size.floor();
        assertEquals(2.0, floored.width());
        assertEquals(3.0, floored.height());
        assertEquals(4.0, floored.depth());
    }

    @Test
    void floorAlreadyInteger() {
        AreaSize size = new AreaSize(2.0, 3.0, 4.0);
        AreaSize floored = size.floor();
        assertEquals(2.0, floored.width());
        assertEquals(3.0, floored.height());
        assertEquals(4.0, floored.depth());
    }

    @Test
    void floorDoesNotMutate() {
        AreaSize size = new AreaSize(2.9, 3.7, 4.1);
        size.floor();
        assertEquals(2.9, size.width());
        assertEquals(3.7, size.height());
        assertEquals(4.1, size.depth());
    }

    @Test
    void toAreaSizeIntegerDimensions() {
        AreaSize size = new AreaSize(2.0, 3.0, 4.0);
        assertEquals(24, size.toAreaSize());
    }

    @Test
    void toAreaSizeFloorsProduct() {
        // floor(2.9 * 3.7 * 4.1) = floor(43.993) = 43
        AreaSize size = new AreaSize(2.9, 3.7, 4.1);
        assertEquals(43, size.toAreaSize());
    }

    @Test
    void toAreaSizeFractionalProduct() {
        // 1.5 * 1.5 * 2.0 = 4.5 -> floor = 4
        AreaSize size = new AreaSize(1.5, 1.5, 2.0);
        assertEquals(4, size.toAreaSize());
    }

    @Test
    void toAreaSizeUnit() {
        AreaSize size = new AreaSize(1.0, 1.0, 1.0);
        assertEquals(1, size.toAreaSize());
    }

    @Test
    void equality() {
        AreaSize size1 = new AreaSize(2.0, 3.0, 4.0);
        AreaSize size2 = new AreaSize(2.0, 3.0, 4.0);
        assertEquals(size1, size2);
    }

    @Test
    void notEqual() {
        AreaSize size1 = new AreaSize(2.0, 3.0, 4.0);
        AreaSize size2 = new AreaSize(2.0, 3.0, 5.0);
        assertNotEquals(size1, size2);
    }

    @Test
    void hashCodeConsistency() {
        AreaSize size1 = new AreaSize(2.0, 3.0, 4.0);
        AreaSize size2 = new AreaSize(2.0, 3.0, 4.0);
        assertEquals(size1.hashCode(), size2.hashCode());
    }

    @Test
    void toStringTest() {
        AreaSize size = new AreaSize(2.0, 3.0, 4.0);
        assertEquals("AreaSize[width=2.0, height=3.0, depth=4.0]", size.toString());
    }

}
