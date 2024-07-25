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

import net.apartium.cocoabeans.collect.WeightSet;

import java.util.*;

/**
 * Helper class to aid working with java collections
 * @author Voigon
 */
public class CollectionHelpers {

    /**
     * Picks a random entry from collection
     * @param collection collection
     * @param <E> collection element type
     * @return null if collection is empty, the first entry if there is only one or a random entry
     */
    public static <E> E randomEntry(Collection<E> collection, Random random) {
        if (collection instanceof WeightSet<E> weightSet) return weightSet.pickOne(random);
        if (collection.isEmpty())
            return null;
        int index = random.nextInt(collection.size());
        return pickEntry(collection, index);
    }

    /**
     * Attempts to pick entry from Collection by index.
     * WARNING: Resulting entry of this object is not guaranteed to be the same across multiple attempts even if given arguments are the same.
     * @param collection collection
     * @param index index to pick
     * @return attempt to pick given element from set
     * @param <E> collection type
     *
     */
    @SuppressWarnings("unchecked")
    public static <E> E pickEntry(Collection<E> collection, int index) {
        if (collection instanceof List<E> list)
            return list.get(index);
        else {
            if (collection.size() < index + 1)
                throw new IndexOutOfBoundsException(String.format("Index %d out of bound for size %d", index, collection.size()));

            for (E e : collection) {
                if (index == 0)
                    return e;
                index--;
            }
            return null; // collection is empty
        }
    }

    /**
     * Checks whether given string contains AT LEAST ONE OF the values in given collection
     * @param collection collection
     * @param value string
     * @param appendToElement string to append to the beginning of each collection element
     * @return true if string contains at least one of the values in given collection, else false.
     */
    public static boolean containsStartsWith(Collection<String> collection, String value, String appendToElement) {
        if (appendToElement == null)
            appendToElement = "";

        if (collection == null || value == null) // Avoid npe
            return false;

        for (String string : collection) {
            if (value.startsWith(appendToElement + string))
                return true;
        }

        return false;
    }

    /**
     * Checks for array content equality, regardless of order
     * @param arr0 first array
     * @param arr1 second array
     * @return true if equals, else false
     */
    public static boolean equalsArray(Object[] arr0, Object[] arr1) {
        return equalsList(
                List.of(arr0),
                List.of(arr1)
        );
    }

    /**
     * Checks for list content equality, regardless of order
     * @param list0 first list
     * @param list1 second list
     * @return true if equals, else false
     */
    public static boolean equalsList(List<?> list0, List<?> list1) {
        if (list0 == list1)
            return true; // quick check

        if (list0.size() != list1.size())
            return false;

        if (list0.isEmpty())
            return true;

        List<Integer> leftIndex = range(0, list0.size(), 1);

        forLoop: for (Object object : list0) {
            Iterator<Integer> iterator = leftIndex.iterator();
            while (iterator.hasNext()) {
                int index = iterator.next();
                if (object.equals(list1.get(index))) {
                    iterator.remove();
                    continue forLoop;
                }
            }

            return false;
        }

        return leftIndex.isEmpty();
    }

    /**
     * Construct a new instance with values consisting of specified range
     * @param start range start
     * @param end range end
     * @param step amount to step each time
     * @return a new linked list instance
     */
    public static List<Integer> range(int start, int end, int step) {
        if (step == 0) throw new IllegalArgumentException("Step cannot be zero");
        if (step < 0) throw new IllegalArgumentException("Step must be positive");

        boolean startBigger = start > end;
        if (startBigger) step = -step;

        List<Integer> result = new ArrayList<>(Math.abs(end - start) / step);
        for (int i = start; (startBigger ? i > end : i < end); i += step) {
            result.add(i);
        }

        return result;
    }

    public static <E> void addElementSorted(List<E> list, E element, Comparator<E> comparator) {
        int index = Collections.binarySearch(list, element, comparator);
        if (index < 0) index = -index - 1;
        list.add(index, element);
    }



}
