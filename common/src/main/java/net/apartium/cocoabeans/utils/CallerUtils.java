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

    private static final Impl impl = getImplementation();

    private static Impl getImplementation() {
        try {
            Class.forName("java.lang.StackWalker");
            return new StackWalkerImpl();
        } catch (Exception e) {
            return new LegacyImpl();
        }
    }

    /**
     * Find caller class, excluding given clazz
     * @param clazz class to exclude from stack trace search
     * @return class instance
     */
    public static Class<?> getCallerClassExcept(Class<?> clazz) {
        return impl.getCallerClassExcept(clazz);
    }

    /**
     * Find caller class, excluding given clazz
     * @param clazz class to exclude from stack trace search
     * @return class name
     */
    public static String getCallerClassNameExcept(Class<?> clazz) {
        return impl.getCallerClassNameExpect(clazz);
    }


    private interface Impl {

        Class<?> getCallerClassExcept(Class<?> clazz);

        String getCallerClassNameExpect(Class<?> clazz);

    }

    private static class StackWalkerImpl implements Impl {

        private static final StackWalker WALKER_WITH_CLASS_REF = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        @Override
        public Class<?> getCallerClassExcept(Class<?> clazz) {
            return WALKER_WITH_CLASS_REF.walk(stream ->
                    stream
                            .map(StackWalker.StackFrame::getDeclaringClass)
                            .filter(cls -> !cls.equals(clazz) && !cls.equals(CallerUtils.class) && !cls.equals(StackWalkerImpl.class))
                            .findFirst()
            ).orElse(null);
        }

        @Override
        public String getCallerClassNameExpect(Class<?> clazz) {
            return StackWalker.getInstance().walk(stream ->
                    stream
                            .map(StackWalker.StackFrame::getClassName)
                            .filter(cls -> (clazz == null || !cls.equals(clazz.getName())) && !cls.equals(CallerUtils.class.getName()) && !cls.equals(StackWalkerImpl.class.getName()))
                            .findFirst()
            ).orElse(null);
        }
    }

    private static class LegacyImpl implements Impl {

        @Override
        public Class<?> getCallerClassExcept(Class<?> clazz) {
            try {
                return Class.forName(getCallerClassNameExpect(clazz));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String getCallerClassNameExpect(Class<?> clazz) {
            // Taken from: https://stackoverflow.com/a/35411095
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String className = clazz.getName();
            boolean classFound = false;
            for (int i = 2; i < stackTrace.length; i++) {
                final StackTraceElement element = stackTrace[i];
                final String callerClassName = element.getClassName();
                // check if class name is the requested class
                if (callerClassName.equals(className))
                    classFound = true;
                else if (classFound)
                    return callerClassName;

            }

            return null;
        }
    }
}
