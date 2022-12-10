/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.utils;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class NumberUtils {

    public static OptionalInt parseInt(String s) {
        if (s == null) return OptionalInt.empty();
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public static OptionalLong parseLong(String s) {
        if (s == null) return OptionalLong.empty();
        try {
            return OptionalLong.of(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    public static OptionalDouble parseDouble(String s) {
        if (s == null) return OptionalDouble.empty();
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    public static OptionalFloat parseFloat(String s) {
        if (s == null) return OptionalFloat.empty();
        try {
            return OptionalFloat.of(Float.parseFloat(s));
        } catch (NumberFormatException e) {
            return OptionalFloat.empty();
        }
    }

}
