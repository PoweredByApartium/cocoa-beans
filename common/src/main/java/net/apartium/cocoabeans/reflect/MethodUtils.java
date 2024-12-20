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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilities to work with java Method class
 * @see Method
 * @author Voigon (Lior S.), Thebotgame (Kfir b.)
 */
public class MethodUtils {

    /**
     * Get all methods from given class
     * by recursion getting declared methods from super class
     *
     * @param clazz class
     * @return set consisting of all methods from given class including super class public & non-public methods
     */
    @ApiStatus.AvailableSince("0.0.38")
    public static Set<Method> getAllMethods(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();

        while (clazz != null && clazz != Object.class) {
            methods.addAll(ReflectionCache.getDeclaredMethods(clazz).toList());
            clazz = clazz.getSuperclass();
        }

        return methods;
    }

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
     * Get all methods from super class / interface
     * @param method method
     * @return all methods from super class / interface
     */
    @ApiStatus.AvailableSince("0.0.29")
    public static Set<Method> getMethodsFromSuperClassAndInterface(Method method) {
        if (method == null)
            return Collections.emptySet();

        Class<?> clazz = method.getDeclaringClass();

        if (clazz == Object.class)
            return Collections.emptySet();

        Set<Method> result = new HashSet<>();

        Method superClassMethod = getSuperClassMethod(method);
        if (superClassMethod != null) {
            result.add(superClassMethod);
            result.addAll(getMethodsFromSuperClassAndInterface(superClassMethod));
        }

        for (Method interfacesMethod : getInterfacesMethods(method)) {
            result.add(interfacesMethod);
            result.addAll(getMethodsFromSuperClassAndInterface(interfacesMethod));
        }

        return result;
    }

    /**
     * Get method from super class via reflection cache
     * @param method method
     * @return super class method, or null if not exists
     */
    @ApiStatus.AvailableSince("0.0.29")
    public static @Nullable Method getSuperClassMethod(@NotNull Method method) {
        Class<?> clazz = method.getDeclaringClass();

        if (clazz.getSuperclass() == null)
            return null;

        return ReflectionCache.getMethod(clazz.getSuperclass(), method.getName(), method.getParameterTypes());
    }

    /**
     * Get methods from interfaces from the reflection cache
     * @param method method
     * @return interfaces methods
     */
    @ApiStatus.AvailableSince("0.0.29")
    public static Method[] getInterfacesMethods(@NotNull Method method) {
        Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
        List<Method> methods = new ArrayList<>();

        for (Class<?> anInterface : interfaces) {
            Method declaredMethod = ReflectionCache.getMethod(anInterface, method.getName(), method.getParameterTypes());
            if (declaredMethod != null) {
                methods.add(declaredMethod);
            }
        }

        return methods.toArray(new Method[0]);
    }

    /**
     * Get interface method from the reflection cache
     * @param method method
     * @param interfaceClass interface class
     * @return interface method or null
     */
    @ApiStatus.AvailableSince("0.0.29")
    public static @Nullable Method getInterfaceMethod(@NotNull Method method, Class<?> interfaceClass) {
        return ReflectionCache.getDeclaredMethod(interfaceClass, method.getName(), method.getParameterTypes());
    }
}
