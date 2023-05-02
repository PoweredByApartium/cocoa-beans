/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.collect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeightSetTest {

    @Test
    void weightSet() {
        WeightSet<Integer> weightSet = new WeightSet<>();
        assertNull(weightSet.pickOne());
        assertThrows(RuntimeException.class, () -> weightSet.pickMany(3));
        assertThrows(RuntimeException.class, () -> weightSet.pickMany(0));
        assertThrows(RuntimeException.class,() -> weightSet.pickMany(-7));
        weightSet.put(7,2.3);
        assertEquals(weightSet.totalWeight(), 2.3);
        assertEquals(weightSet.size(), 1);
        assertEquals(weightSet.getWeight(7), 2.3);
        assertEquals(weightSet.pickOne().intValue(), 7);
        weightSet.put(11, 9);
        assertEquals(weightSet.totalWeight(), 11.3);
        assertEquals(weightSet.size(), 2);
        weightSet.put(15, 91);
        assertEquals(weightSet.totalWeight(), 102.3);
        assertDoesNotThrow(() -> weightSet.pickMany(2));
        assertNotNull(weightSet.pickOne());
        weightSet.remove(11);
        assertEquals(weightSet.totalWeight(), 93.3);
        assertNotNull(weightSet.pickOne());
        assertEquals(weightSet.size(), 2);
        assertThrows(RuntimeException.class, () -> weightSet.put(98, -8));
    }

}
