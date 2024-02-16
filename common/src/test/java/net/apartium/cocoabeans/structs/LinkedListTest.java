/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.structs;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    @Test
    void toLinkedList() {
        LinkedList<String> collect = Stream.of("1", "2", "3", "4")
                .filter(str -> !str.equals("4"))
                .collect(LinkedList.toLinkedList());

        assertTrue(collect.contains("1"));
        assertTrue(collect.contains("2"));
        assertTrue(collect.contains("3"));
        assertFalse(collect.contains("4"));

    }

    @Test
    void size() {
        LinkedList<String> list = new LinkedList<>();
        list.add("1");
        list.add("2");
        assertEquals(2, list.size());
        list.remove("1");
        assertEquals(1, list.size());
        list.remove("2");
        assertTrue(list.isEmpty());
    }

    @Test
    void isEmpty() {
        assertEquals(0, new LinkedList<>().size());
    }

    @Test
    void contains() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        assertTrue(list.contains("2"));
        assertTrue(list.remove("1"));
        assertFalse(list.contains("1"));
    }

    @Test
    void peek() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        assertEquals("1", list.peek().orElse(null));

        LinkedList<String> emptyList = new LinkedList<>();
        assertTrue(emptyList.peek().isEmpty());
    }

    @Test
    void pop() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        Optional<String> pop = list.pop();
        assertTrue(pop.isPresent());
        assertEquals("1", pop.get());
        assertEquals("2", list.peek().orElse(null));
        assertEquals(1, list.size());

        LinkedList<String> emptyList = new LinkedList<>();
        assertTrue(emptyList.pop().isEmpty());
    }

    @Test
    void iterator() {
        LinkedList<String> strings = new LinkedList<>();
        strings.add("1");
        strings.add("2");
        strings.add("1");
        Iterator<String> iterator = strings.iterator();
        int processed = 0;
        while (iterator.hasNext()) {
            processed++;
            String next = iterator.next();
            if (processed == 1)
                assertEquals("1", next);
            else if (processed == 2)
                assertEquals("2", next);
            else if (processed == 3)
                assertEquals("1", next);

        }
        assertEquals(3, processed);
    }

    @Test
    void iteratorSynthetic() {
        LinkedList<String> strings = new LinkedList<>();
        strings.add("1");
        strings.add("2");
        strings.add("1");

        int processed = 0;
        for (String next : strings) {
            processed++;
            if (processed == 1)
                assertEquals("1", next);
            else if (processed == 2)
                assertEquals("2", next);
            else if (processed == 3)
                assertEquals("1", next);
        }

    }

    @Test
    void toArray() {
    }

    @Test
    void testToArray() {
    }

    @Test
    void add() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        assertFalse(list.contains("3"));
    }

    @Test
    void remove() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        assertTrue(list.remove("1"));
        assertFalse(list.contains("1"));
        assertEquals(1, list.size());
        assertFalse(list.remove("3"));
    }

    @Test
    void containsAll() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        assertTrue(list.add("3"));

        LinkedList<String> list2 = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));

        assertTrue(list.containsAll(list2));
    }

    @Test
    void addAll() {
        LinkedList<String> emptyList = new LinkedList<>();

        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));

        assertTrue(emptyList.addAll(list));
        assertEquals(2, emptyList.size());
    }

    @Test
    void removeAll() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        assertTrue(list.add("2"));
        assertTrue(list.add("3"));

        LinkedList<String> list2 = new LinkedList<>();
        assertTrue(list2.add("1"));
        assertTrue(list2.add("2"));

        assertTrue(list.removeAll(list2));
        assertEquals(1, list.size());
    }

    @Test
    void retainAll() {
        assertThrows(UnsupportedOperationException.class, () -> new LinkedList<>().retainAll(List.of()));
    }

    @Test
    void clear() {
        LinkedList<String> list = new LinkedList<>();
        assertTrue(list.add("1"));
        assertTrue(list.add("2"));
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void testEquals() {
        LinkedList<String> withOf = LinkedList.of("kfir", "beta", "lior");
        LinkedList<String> withCtor = new LinkedList<>();
        assertTrue(withCtor.add("kfir"));
        assertTrue(withCtor.add("beta"));
        assertTrue(withCtor.add("lior"));
        assertEquals(withOf, withCtor);
    }

    @Test
    void testToString() {
        LinkedList<String> list = new LinkedList<>();
        list.add("x");
        assertEquals("x -> null", list.toString());
        list.add("y");
        assertEquals("x -> y -> null", list.toString());
        list.add("y");
        assertEquals("x -> y -> y -> null", list.toString());

    }

    @Test
    void range() {
        LinkedList<Integer> range = LinkedList.range(0, 10, 1);
        assertEquals(10, range.size());

    }

    @Test
    void of() {
        LinkedList<String> withOf = LinkedList.of("kfir", "beta", "lior");
        assertTrue(withOf.contains("beta"));
        assertTrue(withOf.contains("kfir"));
        assertTrue(withOf.contains("lior"));
    }
}
