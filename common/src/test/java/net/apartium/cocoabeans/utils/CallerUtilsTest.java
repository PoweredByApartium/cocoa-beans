package net.apartium.cocoabeans.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallerUtilsTest {

    @Test
    void getCallerClassExceptNull() {
        Class<?> callerClass = CallerUtils.getCallerClassExcept(null);
        assertEquals(CallerUtilsTest.class, callerClass);
    }

    @Test
    void getCallerClassNameExceptNull() {
        String callerClass = CallerUtils.getCallerClassNameExcept(null);
        assertEquals(CallerUtilsTest.class.getCanonicalName(), callerClass);
    }

    @Test
    void getCallerClassExceptStub() {
        Class<?> callerClass = CallerUtilsTestHelper.getCallerClassExcept(CallerUtilsTestHelper.class);
        assertEquals(CallerUtilsTest.class, callerClass);
    }

    @Test
    void getCallerClassNameExceptStub() {
        String callerClass = CallerUtilsTestHelper.getCallerClassNameExcept(CallerUtilsTestHelper.class);
        assertEquals(CallerUtilsTest.class.getName(), callerClass);
    }

    @Test
    void getCallerClassExceptStub2() {
        Class<?> callerClass = CallerUtilsTestHelper2.getCallerClassExcept(CallerUtilsTestHelper2.class);
        assertEquals(CallerUtilsTestHelper.class, callerClass);
    }

    @Test
    void getCallerClassNameExceptStub2() {
        String callerClass = CallerUtilsTestHelper2.getCallerClassNameExcept(CallerUtilsTestHelper2.class);
        assertEquals(CallerUtilsTestHelper.class.getName(), callerClass);
    }



    public static class CallerUtilsTestHelper {

        public static Class<?> getCallerClassExcept(Class<?> clazz) {
            return CallerUtils.getCallerClassExcept(clazz);
        }

        public static String getCallerClassNameExcept(Class<?> clazz) {
            return CallerUtils.getCallerClassNameExcept(clazz);
        }

    }

    public static class CallerUtilsTestHelper2 {

        public static Class<?> getCallerClassExcept(Class<?> clazz) {
            return CallerUtilsTestHelper.getCallerClassExcept(clazz);
        }

        public static String getCallerClassNameExcept(Class<?> clazz) {
            return CallerUtilsTestHelper.getCallerClassNameExcept(clazz);
        }

    }



}
