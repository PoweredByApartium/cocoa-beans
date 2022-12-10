/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans;

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
}