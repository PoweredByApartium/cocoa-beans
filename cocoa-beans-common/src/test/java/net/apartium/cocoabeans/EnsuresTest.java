/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Voigon (Lior S.)
 */
class EnsuresTest {

    @Test
    void notEmptyString() {
        RuntimeException e = new RuntimeException("test");

        Ensures.notEmpty("XXXX", "this should not happen");
        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty((String) null, "arg +-");
        }, "arg cannot be empty / null");

        Ensures.notEmpty("XXXX", e);
        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty((String) null, e);
        }, "test");

    }

    @Test
    void testNotEmpty() {
        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty("", new RuntimeException());
        });
    }

    @Test
    void testNotEmptyNullException() {
        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty("", (RuntimeException) null);
        });
    }

    @Test
    void notNull() {
        RuntimeException e = new RuntimeException("test");

        Ensures.notNull(new Object(), "this should not happen");
        assertThrows(NullPointerException.class, () -> {
            Ensures.notNull(null, "arg +-");
        }, "arg cannot be null");

        Ensures.notNull(new Object(), e);

        assertThrows(RuntimeException.class, () -> {
            Ensures.notNull(null, e);
        }, "test");

    }

    @Test
    void testNotNullWithNullMessage() {
        assertThrows(NullPointerException.class, () -> {
            Ensures.notNull(null, (RuntimeException) null);
        });

    }

    @Test
    void notEmptyCollection() {
        RuntimeException e = new RuntimeException("test");

        Ensures.notEmpty(List.of(""), "this should not happen");
        Ensures.notEmpty(Set.of(""), "this should not happen");
        Ensures.notEmpty(Set.of(""), e);

        assertThrows(NullPointerException.class, () -> {
            Ensures.notEmpty((Collection<?>) null, "arg +-");
        }, "arg cannot be empty / null");

        assertThrows(NullPointerException.class, () -> {
            Ensures.notEmpty(Collections.emptyList(), "arg +-");
        }, "arg cannot be empty / null");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty((Collection<?>) null, e);
        }, "test");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty(Collections.emptyList(), e);
        }, "test");


    }

    @Test
    void testNotEmptyMap() {
        RuntimeException e = new RuntimeException("test");
        Ensures.notEmpty(Map.of("", ""), "arg +-");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty((Map<?, ?>) null, "arg +-");
        }, "arg cannot be empty / null");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty(Map.of(), "arg +-");
        }, "arg cannot be empty / null");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty((Map<?, ?>) null, e);
        }, "test");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty(Map.of(), e);
        }, "test");


    }

    @Test
    void testNotEmptyArray() {
        RuntimeException e = new RuntimeException("test");
        Ensures.notEmpty(new String[] { "X" }, "arg +-");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty((String[]) null, "arg +-");
        }, "arg cannot be empty / null");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty(new String[0], "arg +-");
        }, "arg cannot be empty / null");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty((String[]) null, e);
        }, "test");

        assertThrows(RuntimeException.class, () -> {
            Ensures.notEmpty(new String[0], e);
        }, "test");

    }

    @Test
    void largerThan() {
        RuntimeException e = new RuntimeException("test");
        //Ensures.largerThan(1, 1, e);
        Ensures.largerThan(2, 0, e);
        Ensures.largerThan(2, 1, "arg0 +- arg1");

        assertThrows(RuntimeException.class, () -> {
            Ensures.largerThan(1, 2, "arg0 +- arg1");
        }, "arg0 must be larger than arg1");

        assertThrows(RuntimeException.class, () -> {
            Ensures.largerThan(1, 2, e);
        }, "test");

    }

    @Test
    void testLargerThan() {
        assertDoesNotThrow(() -> Ensures.largerThan(10, 12,
                (RuntimeException) null));
    }

    @Test
    void isTrue() {
        RuntimeException e = new RuntimeException("test");

        Ensures.isTrue(true);
        assertThrows(RuntimeException.class, () -> {
            Ensures.isTrue(false, e);
        }, "test");

        assertThrows(RuntimeException.class, () -> {
            Ensures.isTrue(false, "arg +-");
        }, "arg must be true");

    }

    @Test
    void testIsTrueExceptionNull() {
        assertThrows(RuntimeException.class, () -> {
            Ensures.isTrue(false, (RuntimeException) null);
        });
    }

    @Test
    void testIsTrueDoesNotThrow() {
        assertDoesNotThrow(() -> Ensures.isTrue(true, "test"));
    }

    @Test
    void testIsFalse() {
        RuntimeException e = new RuntimeException("test");

        assertThrows(RuntimeException.class, () -> Ensures.isFalse(true));

        assertThrows(RuntimeException.class, () -> {
            Ensures.isFalse(true, e);
        }, "test");

        assertThrows(RuntimeException.class, () -> {
            Ensures.isFalse(true, "arg +-");
        }, "arg must be false");
    }

    @Test
    void testIsFalseExceptionNull() {
        assertThrows(RuntimeException.class,
                () -> Ensures.isFalse(true, (RuntimeException) null));
    }

    @Test
    void testIsFalseDoesNotThrow() {
        assertDoesNotThrow(() -> Ensures.isFalse(false));
    }

    @Test
    void testIsFalseDoesNotThrowWithMessage() {
        assertDoesNotThrow(() -> Ensures.isFalse(false, "arg +-"));
    }

}
