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

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Voigon (Lior S.)
 */
class OptionalFloatTest {

    @Test
    void empty() {
        var empty = OptionalFloat.empty();

        assertTrue(empty.isEmpty());
        assertFalse(empty.isPresent());
        assertThrows(NoSuchElementException.class, empty::getAsFloat, "No value present");
        empty.ifPresent((val) -> {
            assertFalse(true, "Should not reach this part");
        });

        assertThrows(RuntimeException.class, () -> empty.getOrThrow(RuntimeException::new));
        assertSame(empty.filter((val) -> true), empty);
        assertSame(empty.filter((val) -> false), empty);

        assertTrue(empty.mapToObj(Float::valueOf).isEmpty());
        assertEquals(0, empty.orElse(0));
        assertEquals(1, empty.orElseGet(() -> 1));
        assertNotEquals(empty, OptionalFloat.of(1));

    }

    @Test
    void of() {
        var value = OptionalFloat.of(5);
        assertTrue(value.isPresent());
        assertFalse(value.isEmpty());
        assertEquals(5, value.getAsFloat());

        assertSame(OptionalFloat.empty(), value.filter((f) -> false));
        assertSame(value, value.filter((f) -> true));

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        value.ifPresent((val) -> {
            atomicBoolean.set(true);
        });

        assertTrue(atomicBoolean.get());
        assertEquals(5, value.getOrThrow(RuntimeException::new));

        assertTrue(value.mapToObj(Float::valueOf).isPresent());

        assertEquals(5, value.orElse(0));
        assertEquals(5, value.orElseGet(() -> 1));
        assertNotEquals(value, OptionalFloat.of(1));

    }

}
