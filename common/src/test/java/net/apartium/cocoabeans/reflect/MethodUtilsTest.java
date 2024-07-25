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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Voigon (Lior S.)
 */
public class MethodUtilsTest {

    @Test
    void getDeclaredMethods() {
        Set<Method> declaredMethods = MethodUtils.getDeclaredMethods(TestClass.class);
        Assertions.assertEquals(2, declaredMethods.size());

        Set<Method> declaredMethods2 = MethodUtils.getDeclaredMethods(TestClass.class);

        Assertions.assertEquals(declaredMethods, declaredMethods2);
        var iterator = declaredMethods2.iterator();
        while (iterator.hasNext()) {
            Method next = iterator.next();
            Assertions.assertTrue(next.isAccessible());
            for (Method method : declaredMethods) {
                if (next == method)
                    iterator.remove();
            }
        }

    }

    @Test
    void getMethods() {
        Set<Method> methods = MethodUtils.getMethods(TestClass.class);
        long methodsNotFromObject =
                methods.stream().filter(method -> !method.getDeclaringClass().equals(Object.class)).count();

        Assertions.assertEquals(1, methodsNotFromObject);

        Set<Method> methods2 = MethodUtils.getMethods(TestClass.class);

        Assertions.assertEquals(methods, methods2);

        var iterator = methods2.iterator();
        while (iterator.hasNext()) {
            Method next = iterator.next();
            Assertions.assertTrue(next.isAccessible());
            for (Method method : methods) {
                if (next == method)
                    iterator.remove();
            }
        }

    }


    public static class TestClass {

        private void privateMethod() {

        }

        public void publicMethod() {
        }
    }
}
