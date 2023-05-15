/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.reflect;

import net.apartium.cocoabeans.Ensures;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities to work with java Constructor class
 * @see Constructor
 * @author Voigon (Lior S.)
 */
public class ConstructorUtils {

    /**
     * Retrieves all constructors of given class object from library cache
     * @param clazz clazz
     * @return all constructors in given class
     * @param <T> class type
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<Constructor<T>> getDeclaredConstructors(Class<T> clazz) {
        Ensures.isTrue(!Modifier.isInterface(clazz.getModifiers()), "clazz cannot be interface");

        return ReflectionCache.getDeclaredConstructors(clazz)
                .map(i -> (Constructor<T>) i)
                .collect(Collectors.toSet());
    }

}
