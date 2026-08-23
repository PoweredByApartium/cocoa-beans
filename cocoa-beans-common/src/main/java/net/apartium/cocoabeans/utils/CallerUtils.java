/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.utils;

/**
 * A utility class to detect caller
 * @author Voigon
 */
public class CallerUtils {

    private static final StackWalker WALKER_WITH_CLASS_REF = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /**
     * Find caller class, excluding given clazz
     * @param clazz class to exclude from stack trace search
     * @return class instance
     */
    public static Class<?> getCallerClassExcept(Class<?> clazz) {
        return WALKER_WITH_CLASS_REF.walk(stream ->
                stream
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .filter(cls -> !cls.equals(clazz) && !cls.equals(CallerUtils.class))
                        .findFirst()
        ).orElse(null);
    }

    /**
     * Find caller class, excluding given clazz
     * @param clazz class to exclude from stack trace search
     * @return class name
     */
    public static String getCallerClassNameExcept(Class<?> clazz) {
        return StackWalker.getInstance().walk(stream ->
                stream
                        .map(StackWalker.StackFrame::getClassName)
                        .filter(cls -> (clazz == null || !cls.equals(clazz.getName())) && !cls.equals(CallerUtils.class.getName()))
                        .findFirst()
        ).orElse(null);
    }

    private CallerUtils() {}

}
