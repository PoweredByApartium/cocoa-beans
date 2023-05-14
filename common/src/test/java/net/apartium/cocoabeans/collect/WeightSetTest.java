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

    public static final double EPSILON = 0.0001;

    @Test
    void testEmptySet() {
        WeightSet<String> weightSet = new WeightSet<>();
        assertTrue(weightSet.isEmpty());
        weightSet.put("meow", 11);
        assertFalse(weightSet.isEmpty());
        weightSet.remove("meow");
        assertTrue(weightSet.isEmpty());
        assertEquals(0, weightSet.totalWeight());
    }

    @Test
    void testPickOne() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("testing", 81.3);
        assertEquals(81.3, weightSet.totalWeight());
        assertEquals(weightSet.pickOne(), "testing");
        weightSet.put("random", 31.2);
        assertNotNull(weightSet.pickOne());
        assertEquals(112.5, weightSet.totalWeight());

    }

    @Test
    void testPickMany() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("test", 712.68);
        weightSet.put("test1", 26.4);
        weightSet.put("test2", 144.12);
        assertEquals(883.2, weightSet.totalWeight(), EPSILON);

        WeightSet<String> many = weightSet.pickMany(2);
        assertEquals(2, many.size());

        int included = 0;
        if (many.contains("test"))
            included++;
        if (many.contains("test1"))
            included++;
        if (many.contains("test2"))
            included++;

        assertEquals(2, included);
    }

    @Test
    void testPutAll() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.putAll(Map.of(
                "test", 21.0,
                "test2", 81.3,
                "test3", 0.1
        ));
        assertEquals(3, weightSet.size());
        assertTrue(weightSet.containsAll(List.of("test", "test2", "test3")));
        assertEquals(102.4, weightSet.totalWeight(), EPSILON);

    }

    @Test
    void testPutAllWeightSet() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.putAll(Map.of(
                "test", 21.0,
                "test2", 81.3,
                "test3", 0.1
        ));
        WeightSet<String> weightSet2 = new WeightSet<>();
        weightSet2.putAll(weightSet);
        assertEquals(3, weightSet2.size());
        assertTrue(weightSet2.containsAll(List.of("test", "test2", "test3")));
        assertEquals(102.4, weightSet2.totalWeight(), EPSILON);
    }

    @Test
    void testPut() {
        WeightSet<String> weightSet = new WeightSet<>();
        assertThrows(RuntimeException.class,() -> weightSet.put("test", -7));
        assertEquals(0, weightSet.totalWeight());
        weightSet.put("test", 2);
        assertEquals(weightSet.size(), 1);
        assertEquals(2, weightSet.totalWeight());
        assertEquals(weightSet.put("test", 5).orElse(0), 2);
    }

    @Test
    void testContains() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("test", 1);
        weightSet.put("test2", 2);
        weightSet.put("test3", 3);

        assertTrue(weightSet.contains("test2"));
        assertFalse(weightSet.contains("test4"));
        assertEquals(6, weightSet.totalWeight());
    }

    @Test
    void testSize() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.put("test", 62.1);
        weightSet.put("test5", 2.7);
        weightSet.put("test2", 3.1);
        assertEquals(67.9, weightSet.totalWeight(), EPSILON);

        assertEquals(weightSet.size(), 3);
        weightSet.put("test2", 3.4);
        assertEquals(weightSet.size(), 3);
        assertEquals(68.2, weightSet.totalWeight(), EPSILON);

        weightSet.remove("test5");
        assertEquals(weightSet.size(), 2);
        assertEquals(65.5, weightSet.totalWeight(), EPSILON);

    }

    @Test
    void testTotalWeight() {
        WeightSet<String> weightSet = new WeightSet<>();
        weightSet.putAll(Map.of(
                "test", 761.257,
                "test2", 124.6,
                "test3", 65.1
        ));
        assertEquals(950.957, weightSet.totalWeight(), EPSILON);

        assertTrue(Math.abs(weightSet.totalWeight() - 950.957) < 0.001);

        weightSet.put("test4", 316.0);
        assertEquals(1266.957, weightSet.totalWeight(), EPSILON);

        assertTrue(Math.abs(weightSet.totalWeight() - 1266.957) < 0.001);

        weightSet.remove("test2");
        assertTrue(Math.abs(weightSet.totalWeight() - 1142.357) < 0.001);
        assertEquals(1142.357, weightSet.totalWeight(), EPSILON);

    }

    @Test
    void testIterator() {
        WeightSet<String> weightSet = new WeightSet<>();

        assertNotNull(weightSet.iterator());
        assertFalse(weightSet.iterator().hasNext());
        assertThrows(NoSuchElementException.class, () -> weightSet.iterator().next());

        weightSet.put("meow", 1);
        assertEquals(1, weightSet.totalWeight());
        assertTrue(weightSet.iterator().hasNext());
    }

    @Test
    void testRemove() {
        WeightSet<String> weightSet = new WeightSet<>();
        assertFalse(weightSet.remove("test"));
        weightSet.put("test", 18.3);
        assertEquals(18.3, weightSet.totalWeight());
        assertTrue(weightSet.remove("test"));
        assertEquals(0, weightSet.totalWeight());

    }

    @Test
    void testPercentage() {
        WeightSet<String> weightSet = new WeightSet<>();
        String s = "meow";
        weightSet.put(s, 10);
        assertEquals(weightSet.getPercentage(s).orElse(0), 100);
        weightSet.put("wow", 90);
        assertEquals(weightSet.getPercentage("meow").orElse(0), 10);
        weightSet.put("jeff", 60);
        assertEquals(weightSet.getPercentage("jeff").orElse(0), 37.5);
    }

    @Test
    void testNewWithMap() {
        WeightSet<String> weightSet = new WeightSet<>(Map.of(
                "meow", 6.0,
                "woof", 15.0
        ));

        assertEquals(weightSet.size(), 2);
        assertEquals(weightSet.getWeightOrDefault("meow", 0), 6);
    }

    @Test
    void testGetOrDefault() {
        WeightSet<String> weightSet = new WeightSet<>();
        assertEquals(weightSet.getWeightOrDefault("test", 8), 8);
        weightSet.put("test", 9);
        assertEquals(weightSet.getWeightOrDefault("test", 8), 9);
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
        assertEquals(weightSet.getWeight(7).orElse(0), 2.3);
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
