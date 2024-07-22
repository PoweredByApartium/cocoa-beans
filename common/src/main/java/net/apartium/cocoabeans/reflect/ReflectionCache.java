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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Voigon (Lior S.)
 */
/* package-private */ class ReflectionCache {

    private static final ClassValue<ClassCachedData> CLASS_CACHE_REF = new ClassValue<>() {
        @Override
        protected ClassCachedData computeValue(Class<?> type) {
            return new ClassCachedData();
        }
    };

    /* package-private */ static Stream<Constructor> getDeclaredConstructors(Class<?> clazz) {
        ClassCachedData result = getCachedData(clazz);
        if (result.constructors == null) {
            synchronized (result) {
                if (result.constructors == null) {
                    result.constructors = setAccessible(clazz.getDeclaredConstructors());
                }
            }
        }
        return Stream.of(result.constructors);
    }

    /* package-private */ static Stream<Field> getDeclaredFields(Class<?> clazz) {
        ClassCachedData result = getCachedData(clazz);
        if (result.declaredFields == null) {
            synchronized (result) {
                if (result.declaredFields == null) {
                    result.declaredFields = setAccessible(clazz.getDeclaredFields());
                }
            }
        }
        return Stream.of(result.declaredFields);
    }

    /* package-private */ static Stream<Field> getFields(Class<?> clazz) {
        ClassCachedData result = getCachedData(clazz);
        if (result.fields == null) {
            synchronized (result) {
                if (result.fields == null) {
                    result.fields = setAccessible(clazz.getFields());
                }
            }
        }
        return Stream.of(result.fields);
    }

    /* package-private */ static Stream<Method> getMethods(Class<?> clazz) {
        ClassCachedData result = getCachedData(clazz);
        if (result.methods == null) {
            synchronized (result) {
                if (result.methods == null) {
                    result.methods = setAccessible(clazz.getMethods());
                }
            }
        }
        return Stream.of(result.methods);
    }

    /* package-private */ static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>[] params) {
        return getDeclaredMethods(clazz)
                .filter(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), params))
                .findAny()
                .orElse(null);
    }

    /* package-private */ static Stream<Method> getDeclaredMethods(Class<?> clazz) {
        ClassCachedData result = getCachedData(clazz);
        if (result.declaredMethods == null) {
            synchronized (result) {
                if (result.declaredMethods == null) {
                    result.declaredMethods = setAccessible(clazz.getDeclaredMethods());
                }
            }
        }
        return Stream.of(result.declaredMethods);
    }

    private static ClassCachedData getCachedData(Class<?> clazz) {
        return CLASS_CACHE_REF.get(clazz);
    }

    private static <T extends AccessibleObject> T[] setAccessible(T[] array) {
        for (AccessibleObject object : array) {
            object.setAccessible(true);
        }
        return array;
    }

    private static class ClassCachedData {

        private Field[]
                fields,
                declaredFields;

        private Method[]
                methods,
                declaredMethods;

        private Constructor[]
                constructors;

    }
}
