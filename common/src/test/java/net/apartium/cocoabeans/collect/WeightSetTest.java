/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.collect;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class WeightSetTest {

    @Test
    void testEmptySet() {
        WeightSet<String> weightSet = new WeightSet<>();
        assertTrue(weightSet.isEmpty());
        weightSet.put("meow", 11);
        assertFalse(weightSet.isEmpty());
        weightSet.remove("meow");
        assertTrue(weightSet.isEmpty());
    }

    @Test
    void testPickOne() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("testing", 81.3);
        assertEquals(weightSet.pickOne(), "testing");
        weightSet.put("random", 31.2);
        assertNotNull(weightSet.pickOne());
    }

    @Test
    void testPickMany() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("test", 712.68);
        weightSet.put("test1", 26.4);
        weightSet.put("test2", 144.12);
        WeightSet<String> many = weightSet.pickMany(2);
        assert many.size() == 2 && (many.contains("test") || many.contains("test1") || many.contains("test2"));
    }

    @Test
    void testPutAll() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.putAll(Map.of(
                "test", 21.0,
                "test2", 81.3,
                "test3", 0.1
        ));
        assertEquals(weightSet.size(), 3);
        assertTrue(weightSet.containsAll(List.of("test", "test2", "test3")));
    }

    @Test
    void testPut() {
        WeightSet<String> weightSet = new WeightSet<>();
        assertThrows(RuntimeException.class,() -> weightSet.put("test", -7));
        weightSet.put("test", 2);
        assertEquals(weightSet.size(), 1);
    }

    @Test
    void testContains() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("test", 1);
        weightSet.put("test2", 2);
        weightSet.put("test3", 3);

        assertTrue(weightSet.contains("test2"));
        assertFalse(weightSet.contains("test4"));
    }

    @Test
    void testSize() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("test", 62.1);
        weightSet.put("test5", 2.7);
        weightSet.put("test2", 3.1);

        assertEquals(weightSet.size(), 3);
        weightSet.put("test2", 3.4);
        assertEquals(weightSet.size(), 3);

        weightSet.remove("test5");
        assertEquals(weightSet.size(), 2);
    }

    @Test
    void testTotalWeight() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.putAll(Map.of(
                "test", 761.257,
                "test2", 124.6,
                "test3", 65.1
        ));

        assert Math.abs(weightSet.totalWeight() - 950.957) < 0.001;

        weightSet.put("test4", 316.0);
        assert Math.abs(weightSet.totalWeight() - 1266.957) < 0.001;

        weightSet.remove("test2");

        assert Math.abs(weightSet.totalWeight() - 1142.357) < 0.001;
    }

    @Test
    void testIterator() {
        WeightSet<String> weightSet = new WeightSet<>();

        assertNotNull(weightSet.iterator());
        assertFalse(weightSet.iterator().hasNext());
        assertThrows(NoSuchElementException.class, () -> weightSet.iterator().next());

        weightSet.put("meow", 1);

        assert weightSet.iterator().hasNext();
    }

    @Test
    void testRemove() {
        WeightSet<String> weightSet = new WeightSet<>();
        assertFalse(weightSet.remove("test"));
        weightSet.put("test", 18.3);
        assertTrue(weightSet.remove("test"));
    }

    @Test
    void weightSet() {
        WeightSet<Integer> weightSet = new WeightSet<>();
        assertNull(weightSet.pickOne());
        assertThrows(RuntimeException.class, () -> weightSet.pickMany(3));
        assertThrows(RuntimeException.class, () -> weightSet.pickMany(0));
        assertThrows(RuntimeException.class,() -> weightSet.pickMany(-7));
        weightSet.put(7,2.3);
        assertEquals(weightSet.totalWeight(), 2.3);
        assertEquals(weightSet.size(), 1);
        assertEquals(weightSet.getWeight(7), 2.3);
        assertEquals(weightSet.pickOne().intValue(), 7);
        weightSet.put(11, 9);
        assertEquals(weightSet.totalWeight(), 11.3);
        assertEquals(weightSet.size(), 2);
        weightSet.put(15, 91);
        assertEquals(weightSet.totalWeight(), 102.3);
        assertDoesNotThrow(() -> weightSet.pickMany(2));
        assertNotNull(weightSet.pickOne());
        weightSet.remove(11);
        assertEquals(weightSet.totalWeight(), 93.3);
        assertNotNull(weightSet.pickOne());
        assertEquals(weightSet.size(), 2);
        assertThrows(RuntimeException.class, () -> weightSet.put(98, -8));
    }

}
