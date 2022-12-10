/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Voigon (Lior S.)
 */
class FieldUtilsTest {

    @Test
    void getDeclaredFieldsByExactType() {
        var result = FieldUtils.getDeclaredFieldsByExactType(TestClass.class, String.class);
        System.out.println(result);
        assertEquals(3, result.size());
        assertEquals(Set.of("normalField", "declaredField", "declaredField0"), result.stream().map(Field::getName).collect(Collectors.toSet()));

        // Ensure fields are cached by checking their identity
        var result2 = FieldUtils.getDeclaredFieldsByExactType(TestClass.class, String.class);
        var iterator = result2.iterator();
        while (iterator.hasNext()) {
            Field next = iterator.next();
            for (Field field : result) {
                if (next == field)
                    iterator.remove();
            }
        }

        assertEquals(0, result2.size());


    }

    @Test
    void getFieldsByExactType() {
        var result = FieldUtils.getFieldsByExactType(TestClass.class, String.class);
        assertEquals(1, result.size());
        assertEquals(Set.of("normalField"), result.stream().map(Field::getName).collect(Collectors.toSet()));

        // Ensure fields are cached by checking their identity
        var result2 = FieldUtils.getFieldsByExactType(TestClass.class, String.class);
        var iterator = result2.iterator();
        while (iterator.hasNext()) {
            Field next = iterator.next();
            for (Field field : result) {
                if (next == field)
                    iterator.remove();
            }
        }

        assertEquals(0, result2.size());

    }

    public static class TestClass {

        public String normalField;

        private String declaredField;

        private final String declaredField0 = "4";

        private int declaredFieldExcluded;

    }

}