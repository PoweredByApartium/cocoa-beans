/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.collect;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Voigon (Lior S.)
 */
class ImmutableByteArrayListTest {

    @Test
    void ofEmpty() {
        var empty = ImmutableByteArrayList.of();
        assertSame(empty, ImmutableByteArrayList.of());
        assertEquals(empty, ImmutableByteArrayList.of());
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
        assertFalse(empty.contains((byte) 0));
        assertFalse(empty.iterator().hasNext());
        assertEquals(0, empty.stream().count());
        assertEquals(0, empty.toArray().length);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            empty.get(0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            empty.get(0);
        });

        assertEquals(-1, empty.indexOf((byte) 0));
        assertEquals(-1, empty.lastIndexOf((byte) 0));

    }

    @Test
    void testOf() {
        var list = ImmutableByteArrayList.of((byte) 1, (byte) 2);
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());
        assertTrue(list.contains((byte) 1));
        assertTrue(list.contains((byte) 2));
        assertFalse(list.contains((byte) 0));

        assertEquals(2, list.stream().count());
        assertEquals(2, list.toArray().length);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(4);
        });


        List<Byte> badList = new ArrayList<>();
        list.forEach(badList::add);
        assertEquals(ImmutableByteArrayList.makeBetter(badList), list);

        badList.clear();
        assertNotEquals(ImmutableByteArrayList.makeBetter(badList), list);

        var iterator = list.iterator();
        while (iterator.hasNext()) {
            Byte next = iterator.next();
            assertNotNull(next);
            badList.add(next);
        }

        assertThrows(IndexOutOfBoundsException.class, iterator::next);

        assertEquals(list, ImmutableByteArrayList.makeBetter(badList));

        assertEquals((byte) 1, list.get(0));
        assertEquals((byte) 2, list.get(1));

        assertEquals(0, list.indexOf((byte) 1));
        assertEquals(0, list.lastIndexOf((byte) 1));
        assertEquals(1, list.indexOf((byte) 2));
        assertEquals(1, list.lastIndexOf((byte) 2));
        assertEquals(-1, list.indexOf((byte) 32));

    }

    @Test
    void lastIndexOf() {
        var list = ImmutableByteArrayList.of((byte) 1, (byte) 2, (byte) 4, (byte) 1);
        assertEquals(3, list.lastIndexOf((byte) 1));
        assertEquals(0, list.indexOf((byte) 1));
    }

}