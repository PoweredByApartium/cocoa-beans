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

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Voigon (Lior S.)
 */
public class FieldUtils {

    /**
     * Get all declared fields whose type is the same of given fieldType
     * @param clazz clazz to lookup fields from
     * @param fieldType expected field types
     * @return a mutable set of fields found
     */
    public static Set<Field> getDeclaredFieldsByExactType(Class<?> clazz, Class<?> fieldType) {
        return ReflectionCache.getDeclaredFields(clazz)
                .filter(field -> field.getType().equals(fieldType))
                .collect(Collectors.toSet());
    }

    /**
     * Get all fields whose type is the same of given fieldType
     * @param clazz clazz to lookup fields from
     * @param fieldType expected field types
     * @return a mutable set of fields found
     */
    public static Set<Field> getFieldsByExactType(Class<?> clazz, Class<?> fieldType) {
        return ReflectionCache.getFields(clazz)
                .filter(field -> field.getType().equals(fieldType))
                .collect(Collectors.toSet());
    }

}
