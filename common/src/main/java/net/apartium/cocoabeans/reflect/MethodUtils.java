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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities to work with java Method class
 * @see Method
 * @author Voigon (Lior S.)
 */
public class MethodUtils {

    /**
     * Get declared methods from given class
     * @param clazz class
     * @return set consisting of all declared methods from given class
     */
    public static Set<Method> getDeclaredMethods(Class<?> clazz) {
        return ReflectionCache.getDeclaredMethods(clazz)
                .collect(Collectors.toSet());
    }

    /**
     * Get methods from given class
     * @param clazz class
     * @return set consisting of all methods from given class
     */
    public static Set<Method> getMethods(Class<?> clazz) {
        return ReflectionCache.getMethods(clazz)
                .collect(Collectors.toSet());
    }

    /**
     * Get method from super class
     * @param method method
     * @return super class method if exists
     */
    public static @Nullable Method getSuperClassMethod(@NotNull Method method) {
        Class<?> clazz = method.getDeclaringClass();

        if (clazz.getSuperclass() == null)
            return null;

        try {
            return clazz.getSuperclass().getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
