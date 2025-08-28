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

import net.apartium.cocoabeans.utils.OptionalFloat;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * A class consisting of string utility methods
 * @author Voigon
 */
public class StringHelpers {

    /**
     * Attempts to parse given string arg as a number.
     *
     * @param arg string argument
     * @return empty if parsing was not successful, otherwise resulting number boxed with option
     */
    public static OptionalInt parseInteger(String arg) {
        try {
            return OptionalInt.of(Integer.parseInt(arg));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    /**
     * Attempts to parse given string arg as a number.
     *
     * @param arg string argument
     * @return empty if parsing was not successful, otherwise resulting number boxed with option
     */
    public static OptionalDouble parseDouble(String arg) {
        try {
            return OptionalDouble.of(Double.parseDouble(arg));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    /**
     * Attempts to parse given string arg as a number.
     *
     * @param arg string argument
     * @return empty if parsing was not successful, otherwise resulting number boxed with option
     */
    public static OptionalFloat parseFloat(String arg) {
        try {
            return OptionalFloat.of(Float.parseFloat(arg));
        } catch (NumberFormatException e) {
            return OptionalFloat.empty();
        }
    }

    /**
     * Attempts to parse given string arg as a number.
     *
     * @param arg string argument
     * @return empty if parsing was not successful, otherwise resulting number boxed with option
     */
    public static OptionalLong parseLong(String arg) {
        try {
            return OptionalLong.of(Long.parseLong(arg));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    /**
     * Checks if given string is null or empty.
     * @param arg given string
     * @return true if null or empty, else false
     */
    public static boolean isNullOrEmpty(String arg) {
        return arg == null || arg.isEmpty();
    }
}
