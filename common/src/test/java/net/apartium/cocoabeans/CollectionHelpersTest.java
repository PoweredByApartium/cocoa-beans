/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Voigon (Lior S.)
 */
class CollectionHelpersTest {

    @Test
    void randomEntry() {
        Random random = new Random();
        assertNull(CollectionHelpers.randomEntry(Set.of(), random));
        assertNull(CollectionHelpers.randomEntry(List.of(), random));
        assertNull(CollectionHelpers.randomEntry(new ArrayList<>(), random));
        assertNull(CollectionHelpers.randomEntry(Collections.emptySet(), random));

        Set<String> testSet = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            testSet.add(UUID.randomUUID().toString());
        }

        String lastResult = CollectionHelpers.randomEntry(testSet, random);
        assertNotNull(lastResult);
        while (true) {
            if (!lastResult.equals(CollectionHelpers.randomEntry(testSet, random)))
                break;
        }

    }

    @Test
    void equalsCollections() {
        List<Integer> list = List.of(1);
        assertTrue(CollectionHelpers.equalsCollections(list, list));
        assertFalse(CollectionHelpers.equalsCollections(list, null));
        assertFalse(CollectionHelpers.equalsCollections(null, list));
        assertTrue(CollectionHelpers.equalsCollections(null, null));

        assertFalse(CollectionHelpers.equalsCollections(list, List.of()));
        assertFalse(CollectionHelpers.equalsCollections(list, List.of(1, 2)));
        assertTrue(CollectionHelpers.equalsCollections(list, Set.of(1)));
        assertFalse(CollectionHelpers.equalsCollections(list, List.of(2)));

    }

    @Test
    void pickEntry() {
        Set<String> set = Set.of("1", "2", "3");
        List<String> list = List.of("1", "2", "3");

        // ensure consistent result in list
        assertEquals(
                CollectionHelpers.pickEntry(list, 0),
                CollectionHelpers.pickEntry(list, 0)
        );

        assertNotNull(CollectionHelpers.pickEntry(set, 0));
        assertNotNull(CollectionHelpers.pickEntry(set, 1));

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> CollectionHelpers.pickEntry(set, 4)
        );

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> CollectionHelpers.pickEntry(list, 4)
        );

    }

    @Test
    void containsStartsWith() {
        List<String> list = List.of("XX", "DD", "D");
        assertTrue(CollectionHelpers.containsStartsWith(list, "XX", ""));
        assertTrue(CollectionHelpers.containsStartsWith(list, "XX", null));
        assertTrue(CollectionHelpers.containsStartsWith(list, "XD", "X"));
        assertFalse(CollectionHelpers.containsStartsWith(list, "XD", "XX"));

    }

    @Test
    void equalsArray() {
        Object[] empty = new Object[0];
        Assertions.assertTrue(CollectionHelpers.equalsArray(empty, empty));
        Object[] arr0 = new Object[]{"1", "2", "3"};
        Object[] arr1 = new Object[]{"1", "2", new String("3")};
        Assertions.assertTrue(CollectionHelpers.equalsArray(arr0, arr1));
        Assertions.assertFalse(CollectionHelpers.equalsArray(arr0, empty));

        Integer[] intArr0 = new Integer[] {1,3,2};
        Integer[] intArr1 = new Integer[] {1,2,3};
        Assertions.assertTrue(CollectionHelpers.equalsArray(intArr0, intArr1));

        String[] strArr0 = new String[] {"1", "2", "3"};
        String[] strArr1 = new String[] {"3", "2", "1"};
        Assertions.assertTrue(CollectionHelpers.equalsArray(strArr0, strArr1));

    }

    @Test
    void testSortedArray() {
        List<Integer> list = new ArrayList<>();
        Comparator<Integer> comparator = Integer::compare;

        CollectionHelpers.addElementSorted(list, 1, comparator);
        assertEquals(list, List.of(1));
        CollectionHelpers.addElementSorted(list, 3, comparator);
        assertEquals(list, List.of(1, 3));
        CollectionHelpers.addElementSorted(list, 2, comparator);
        assertEquals(list, List.of(1, 2, 3));
        CollectionHelpers.addElementSorted(list, 5, comparator);
        assertEquals(list, List.of(1, 2, 3, 5));
        CollectionHelpers.addElementSorted(list, -2, comparator);
        assertEquals(list, List.of(-2, 1, 2, 3, 5));
        CollectionHelpers.addElementSorted(list, 1, comparator);
        assertEquals(list, List.of(-2, 1, 1, 2, 3, 5));
        CollectionHelpers.addElementSorted(list, 1, comparator);
        assertEquals(list, List.of(-2, 1, 1, 1, 2, 3, 5));
        CollectionHelpers.addElementSorted(list, 1, comparator);
        assertEquals(list, List.of(-2, 1, 1, 1, 1, 2, 3, 5));
    }

    @Test
    void testTheTest() {
        assertEquals(List.of("1", "2", "3"), List.of("1", "2", "3"));
        assertNotEquals(List.of("1", "2", "3"), List.of("3", "1", "2"));
    }

    boolean equal(List<?> arr0, List<?> arr1) {
        if (arr0.size() != arr1.size())
            return false;

        for (int i = 0; i < arr0.size(); i++) {
            if (!arr0.get(i).equals(arr1.get(i)))
                return false;
        }

        return true;
    }

    @Test
    void testMergeInto() {
        Map<String, String> map = new HashMap<>();

        CollectionHelpers.mergeInto(map, Map.of("test", "value0"));

        assertEquals(Map.of("test", "value0"), map);

        CollectionHelpers.mergeInto(map, Map.of("test", "value1"));

        assertEquals(Map.of("test", "value0"), map);

        map = new HashMap<>();
        CollectionHelpers.mergeInto(map, Map.of("test", "value0", "test2", "value3", "test0", "value1", "test1", "value2"));

        assertEquals(Map.of("test", "value0", "test0", "value1", "test1", "value2", "test2", "value3"), map);


    }

    @Test
    void isSorted() {
        assertTrue(CollectionHelpers.isSorted(List.of(0, 1, 2, 3), Integer::compareTo));
        assertTrue(CollectionHelpers.isSorted(List.of(), Integer::compareTo));
        assertTrue(CollectionHelpers.isSorted(List.of(-21, -3, 7, 16, 74), Integer::compareTo));

        assertTrue(CollectionHelpers.isSorted(List.of(5, 4, 3, 2, 1), (a, b) -> b - a));
        assertFalse(CollectionHelpers.isSorted(List.of(5, 4, 3, 2, 1, 4), (a, b) -> b - a));

    }

}
