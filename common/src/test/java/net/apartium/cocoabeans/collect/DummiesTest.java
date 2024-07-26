/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.collect;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Voigon (Lior S.)
 */
class DummiesTest {

    @Test
    void dummyMap() {
        var map = Dummies.dummyMap();
        assertEquals(0, map.size());
        assertNull(map.put("", null));
        assertEquals(0, map.size());
        assertNull(map.remove(""));
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey("X"));
        assertFalse(map.containsValue("Z"));
        assertNull(map.get("X"));
    }

    @Test
    void dummySet() {
        var set = Dummies.dummySet();
        assertEquals(0, set.size());
        assertFalse(set.add("AA"));
        assertEquals(0, set.size());
        assertFalse(set.remove(""));
        assertTrue(set.isEmpty());
        assertFalse(set.contains("X"));
        assertFalse(set.containsAll(Set.of("X", "Z")));
        assertFalse(set.iterator().hasNext());
        assertEquals(0, set.toArray().length);
        assertEquals(0, set.toArray(new Object[0]).length);
        assertFalse(set.addAll(Arrays.asList("x", "y")));
        assertFalse(set.retainAll(Arrays.asList("x", "y")));
        assertFalse(set.removeAll(Arrays.asList("x", "y")));
    }

    @Test
    void dummyList() {
        var list = Dummies.dummyList();
        assertEquals(0, list.size());
        assertFalse(list.add("AA"));
        assertEquals(0, list.size());
        assertFalse(list.remove(""));
        assertTrue(list.isEmpty());
        assertFalse(list.contains("X"));
        assertNull(list.get(0));
        assertFalse(list.containsAll(Arrays.asList("X", "Z")));
        assertFalse(list.iterator().hasNext());
        assertEquals(0, list.toArray().length);
        assertEquals(0, list.toArray(new Object[0]).length);
        assertFalse(list.addAll(Arrays.asList("x", "y")));
        assertFalse(list.retainAll(Arrays.asList("x", "y")));
        assertFalse(list.removeAll(Arrays.asList("x", "y")));
    }

    @Test
    void dummyCollection() {
        var collection = Dummies.dummyCollection();
        assertEquals(0, collection.size());
        assertFalse(collection.add("AA"));
        assertEquals(0, collection.size());
        assertFalse(collection.remove(""));
        assertTrue(collection.isEmpty());
        assertFalse(collection.contains("X"));
        assertFalse(collection.containsAll(Arrays.asList("X", "Z")));
        assertFalse(collection.iterator().hasNext());
        assertEquals(0, collection.toArray().length);
        assertEquals(0, collection.toArray(new Object[0]).length);
        assertFalse(collection.addAll(Arrays.asList("x", "y")));
        assertFalse(collection.retainAll(Arrays.asList("x", "y")));
        assertFalse(collection.removeAll(Arrays.asList("x", "y")));
    }
}
