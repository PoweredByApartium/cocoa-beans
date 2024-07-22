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

import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Utilities to work with Java classes
 * @see Class
 * @author Voigon (Lior S.), ikfir
 */
public class ClassUtils {

    /**
     * Recursively find all super classes and super interfaces for given class object.
     * @param clazz clazz
     * @return a collection of all super classes and super interfaces of given class
     * @param <T> type arg for convenience
     */
    @ApiStatus.AvailableSince("0.0.29")
    public static <T> Collection<Class<? super T>> getSuperClassAndInterfaces(Class<T> clazz) {
        if (clazz == null || clazz == Object.class)
            return Collections.emptyList();

        Set<Class<? super T>> set = new HashSet<>();
        set.add(clazz);

        if (!clazz.isInterface()) {
            set.addAll(getSuperClassAndInterfaces(clazz.getSuperclass()));
        }

        for (Class<?> anInterface : clazz.getInterfaces())
            set.addAll(cast(getSuperClassAndInterfaces(anInterface)));

        return set;
    }

    private static <IKFIR> IKFIR cast(Object object) {
        return (IKFIR) object;
    }

}
