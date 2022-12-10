/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.reflect;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

/**
 * @author Voigon (Lior S.)
 */
/* package-private */ class ReflectionCache {

    private static final Map<Class<?>, ClassCachedData>
            cache = Collections.synchronizedMap(new WeakHashMap<>());

    /* package-private */ static Stream<Field> getDeclaredFields(Class<?> clazz) {
        ClassCachedData result = getCachedData(clazz);
        if (result.declaredFields == null)
            result.declaredFields = clazz.getDeclaredFields();
        return Stream.of(result.declaredFields);
    }

    /* package-private */ static Stream<Field> getFields(Class<?> clazz) {
        ClassCachedData result = getCachedData(clazz);
        if (result.fields == null)
            result.fields = clazz.getFields();
        return Stream.of(result.fields);
    }

    private static ClassCachedData getCachedData(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, cl -> new ClassCachedData());
    }

    private static class ClassCachedData {

        private Field[]
                fields,
                declaredFields;

    }
}
