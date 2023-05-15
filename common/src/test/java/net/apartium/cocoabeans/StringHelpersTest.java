/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Voigon (Lior S.)
 */
class StringHelpersTest {

    @Test
    void parseIntegerFail() {
        var parsed = StringHelpers.parseInteger("X");
        assertTrue(parsed.isEmpty());
    }

    @Test
    void parseInteger() {
        var parsed = StringHelpers.parseInteger("10");
        assertTrue(parsed.isPresent());
        assertEquals(10, parsed.getAsInt());
    }

    @Test
    void parseDoubleFail() {
        var parsed = StringHelpers.parseDouble("X");
        assertTrue(parsed.isEmpty());
    }

    @Test
    void parseDouble() {
        var parsed = StringHelpers.parseDouble("5.3");
        assertTrue(parsed.isPresent());
        assertEquals(5.3, parsed.getAsDouble());
    }

    @Test
    void parseFloatFail() {
        var parsed = StringHelpers.parseFloat("X");
        assertTrue(parsed.isEmpty());
    }

    @Test
    void parseFloat() {
        var parsed = StringHelpers.parseFloat("1.2");
        assertTrue(parsed.isPresent());
        assertEquals(1.2, parsed.getAsFloat(), 0.0001);
    }

    @Test
    void parseLongFail() {
        var parsed = StringHelpers.parseLong("X");
        assertTrue(parsed.isEmpty());
    }

    @Test
    void parseLong() {
        var parsed = StringHelpers.parseLong("5555");
        assertTrue(parsed.isPresent());
        assertEquals(5555, parsed.getAsLong());
    }

    @Test
    void isNullOrEmpty() {
        assertTrue(StringHelpers.isNullOrEmpty(null));
        assertTrue(StringHelpers.isNullOrEmpty(""));
        assertFalse(StringHelpers.isNullOrEmpty("XXX"));
    }

}