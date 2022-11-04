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

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Misc utility methods to work with java collection's api
 * @author Voigon (Lior S.)
 */
public class CollectionHelpers {

    /**
     * Gets a random entry from collection
     * @param collection collection
     * @param <E> collection element type
     * @return null if collection is empty, the first entry if there is only one or a random entry
     */
    public static <E> E randomEntry(Collection<E> collection) {
        if (collection.isEmpty())
            return null;

        if (collection.size() == 1)
            return collection.iterator().next();

        int index = new Random().nextInt(collection.size());
        if (collection instanceof List<E> list)
            return list.get(index);
        else
            return pickEntry(collection, index);
    }

    @SuppressWarnings("unchecked")
    private static <E> E pickEntry(Collection<E> collection, int index) {
        return (E) collection.toArray()[index];
    }

    /**
     * Checks whether or not given string contains AT LEAST ONE OF the values in given collection
     * @param collection collection
     * @param value string
     * @return true if string contains at least one of the values in given collection, else false.
     */
    public static boolean containsStartsWith(Collection<String> collection, String value, String appendToElement) {
        if (collection == null || value == null) // Avoid npe
            return false;

        for (String string : collection) {
            if (value.startsWith(appendToElement + string))
                return true;
        }

        return false;
    }

}
