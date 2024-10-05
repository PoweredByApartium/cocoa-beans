/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.structs;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MinecraftVersionTest {

    @Test
    void equalVersions() {
        MinecraftVersion version = MinecraftVersion.V1_9;
        assertEquals(version, MinecraftVersion.getVersion(1, 9, 0));
        assertNotEquals(version, MinecraftVersion.getVersion(1, 10, 0));
    }

    @Test
    void isUnknown() {
        MinecraftVersion version = MinecraftVersion.V1_9;
        assertNotEquals(version, MinecraftVersion.UNKNOWN);
        assertFalse(version.isUnknown());

        version = MinecraftVersion.UNKNOWN;
        assertTrue(version.isUnknown());
    }

    @Test
    void higherThan() {
        MinecraftVersion mySeverVersion = MinecraftVersion.V1_12_2;

        assertTrue(mySeverVersion.isHigherThan(MinecraftVersion.V1_12_1));
        assertTrue(mySeverVersion.isHigherThan(MinecraftVersion.V1_12));
        assertTrue(mySeverVersion.isHigherThan(MinecraftVersion.V1_9_4));

        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_12_2));
        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_13));
        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_13_1));
        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_13_2));

        mySeverVersion = MinecraftVersion.V1_20_2;

        assertTrue(mySeverVersion.isHigherThan(MinecraftVersion.V1_20));
        assertTrue(mySeverVersion.isHigherThan(MinecraftVersion.V1_20_1));

        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_20_2));
        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_20_3));
        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_20_4));
        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_20_5));
        assertFalse(mySeverVersion.isHigherThan(MinecraftVersion.V1_20_6));

        mySeverVersion = MinecraftVersion.getVersion(2, 21, 4);

        assertTrue(mySeverVersion.isHigherThan(MinecraftVersion.V1_21));
        assertFalse(MinecraftVersion.V1_21.isHigherThan(mySeverVersion));
    }

    @Test
    void higherThanOrEqual() {
        MinecraftVersion mySeverVersion = MinecraftVersion.V1_12_2;

        assertTrue(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_12_1));
        assertTrue(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_12));
        assertTrue(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_9_4));
        assertTrue(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_12_2));

        assertFalse(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_13));
        assertFalse(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_13_1));
        assertFalse(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_13_2));

        mySeverVersion = MinecraftVersion.V1_20_1;

        assertFalse(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_20_2));

        mySeverVersion = MinecraftVersion.getVersion(2, 21, 4);

        assertTrue(mySeverVersion.isHigherThanOrEqual(MinecraftVersion.V1_21));
        assertFalse(MinecraftVersion.V1_21.isHigherThanOrEqual(mySeverVersion));
    }

    @Test
    void lowerThan() {
        MinecraftVersion mySeverVersion = MinecraftVersion.V1_12_2;

        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_12_1));
        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_12));
        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_9_4));
        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_12_2))
        ;
        assertTrue(mySeverVersion.isLowerThan(MinecraftVersion.V1_13));
        assertTrue(mySeverVersion.isLowerThan(MinecraftVersion.V1_13_1));
        assertTrue(mySeverVersion.isLowerThan(MinecraftVersion.V1_13_2));

        mySeverVersion = MinecraftVersion.V1_20_2;

        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_20));
        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_20_1));
        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_20_2));

        assertTrue(mySeverVersion.isLowerThan(MinecraftVersion.V1_20_3));
        assertTrue(mySeverVersion.isLowerThan(MinecraftVersion.V1_20_4));
        assertTrue(mySeverVersion.isLowerThan(MinecraftVersion.V1_20_5));
        assertTrue(mySeverVersion.isLowerThan(MinecraftVersion.V1_20_6));

        mySeverVersion = MinecraftVersion.getVersion(2, 21, 4);

        assertFalse(mySeverVersion.isLowerThan(MinecraftVersion.V1_21));
        assertTrue(MinecraftVersion.V1_21.isLowerThan(mySeverVersion));
    }

    @Test
    void lowerThanOrEqual() {
        MinecraftVersion mySeverVersion = MinecraftVersion.V1_12_2;

        assertTrue(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_12_2));

        assertFalse(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_12_1));
        assertFalse(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_12));
        assertFalse(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_9_4));

        assertTrue(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_13));
        assertTrue(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_13_1));
        assertTrue(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_13_2));

        mySeverVersion = MinecraftVersion.V1_20_1;

        assertTrue(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_20_2));

        mySeverVersion = MinecraftVersion.getVersion(2, 21, 4);

        assertFalse(mySeverVersion.isLowerThanOrEqual(MinecraftVersion.V1_21));
        assertTrue(MinecraftVersion.V1_21.isLowerThanOrEqual(mySeverVersion));
    }


    @Test
    void unknownProtocol() {
        MinecraftVersion mySeverVersion = MinecraftVersion.getVersion(2, 21, 4);
        assertFalse(mySeverVersion.isProtocolKnown());
        assertFalse(mySeverVersion.isUnknown());

        mySeverVersion = MinecraftVersion.V1_8_8;
        assertTrue(mySeverVersion.isProtocolKnown());
        assertFalse(mySeverVersion.isUnknown());
    }

    @Test
    void hashCodeTest() {
        MinecraftVersion mySeverVersion = MinecraftVersion.V1_8_8;

        assertEquals(Objects.hash(mySeverVersion.major(), mySeverVersion.update(), mySeverVersion.minor()), mySeverVersion.hashCode());
    }

}