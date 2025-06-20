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

import org.jetbrains.annotations.ApiStatus;
import java.util.Collection;
import java.util.Map;

/**
 * Helper class used to validate conditions
 * @author Voigon
 */
public class Ensures {

    /**
     * Validates if a String is empty or not
     *
     * @param string the String instance to perform the validation on
     * @param ex the exception to throw if the String is empty
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void notEmpty(String string, RuntimeException ex) {
        if (ex == null)
            ex = new NullPointerException();

        if (StringHelpers.isNullOrEmpty(string))
            throw ex;
    }

    /**
     * Checks if an object is null.
     *
     * @param obj the object instance to check
     * @param ex  the exception to throw if the object is null, if null will be replaced by npe
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void notNull(Object obj, RuntimeException ex) {
        if (obj == null)
            throw ex == null ? new NullPointerException() : ex;

    }

    /**
     * Makes sure that given collection is not null or empty
     *
     * @param collection the collection to check
     * @param ex the exception to throw if the object does not match requested precondition
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void notEmpty(Collection<?> collection, RuntimeException ex) {
        notNull(collection, ex);
        if (collection.isEmpty())
            throw ex;

    }

    /**
     * Makes sure that given collection is not null or empty
     *
     * @param collection the collection to check
     * @param message the message, "+-" will be replaced to "cannot be null or empty"
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, new NullPointerException(message.replaceAll("\\+-", "cannot be null or empty")));
    }

    /**
     * Makes sure that given map is not null or empty
     *
     * @param map  the map to check
     * @param ex the exception to throw if the object does not match requested precondition
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void notEmpty(Map<?, ?> map, RuntimeException ex) {
        notNull(ex, "ex +-");
        notNull(map, ex);

        if (map.isEmpty())
            throw ex;

    }

    /**
     * Makes sure that given map is not null or empty
     *
     * @param map the map object to check
     * @param message the message, "+-" will be replaced to "cannot be null or empty"
     * @throws RuntimeException if argument does not match requested precondition
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        notEmpty(map, new RuntimeException(message.replaceAll("\\+-", "cannot be null or empty")));
    }

    /**
     * Make sure that given array is not null or empty
     *
     * @param array the array to check
     * @param ex the exception to throw if the object is invalid
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void notEmpty(Object[] array, RuntimeException ex) {
        notNull(array, ex);

        if (array.length == 0)
            throw ex;

    }

    /**
     * Makes sure that given array is not null or empty
     *
     * @param array the array to check
     * @param message the message, "+-" will be replaced to "cannot be null or empty"
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void notEmpty(Object[] array, String message) {
        notEmpty(array, new RuntimeException(message.replaceAll("\\+-", "cannot be null or empty")));
    }

    /**
     * Make sure an object is not null.
     *
     * @param obj the object to check
     * @param message the message to throw with the exception if the object is null, +- will be replaced with "cannot be null"
     * @throws NullPointerException ex if argument does not match requested precondition
     */
    public static void notNull(Object obj, String message) {
        notNull(obj, new NullPointerException(message.replaceAll("\\+-", "cannot be null")));
    }

    /**
     * Makes sure given string is not null or empty
     *
     * @param string the String instance to validate on
     * @param message the message to throw with the exception if the String is empty, "+-" will be replaced to "cannot be null or empty"
     * @throws RuntimeException if argument does not match requested precondition
     */
    public static void notEmpty(String string, String message) {
        notEmpty(string, new RuntimeException(message.replaceAll("\\+-", "cannot be empty / null")));
    }

    /**
     * Makes sure given number is larger than second given number.
     *
     * @param num1 the first number
     * @param num2 the second number
     * @param ex the exception to throw if not larger than
     * @throws RuntimeException if argument does not match requested precondition
     */
    public static void largerThan(int num1, int num2, RuntimeException ex) {
        if (num1 < num2)
            if (ex != null)
                throw ex;
    }

    /**
     * Makes sure given number is larger than second given number.
     *
     * @param num1 the first number
     * @param num2 the second number
     * @param message the message, "+-" will be replaced to "must be larger than"
     * @throws RuntimeException if argument does not match requested precondition
     */
    public static void largerThan(int num1, int num2, String message) {
        largerThan(num1, num2, new RuntimeException(message.replaceAll("\\+-", "must be larger than")));

    }

    /**
     * Makes sure given condition is true
     *
     * @param bool the condition
     * @throws RuntimeException if argument does not match requested precondition
     */
    public static void isTrue(boolean bool) {
        isTrue(bool, new RuntimeException());

    }

    /**
     * Makes sure given condition is true
     *
     * @param bool the condition
     * @param message the message to send with the exception, "+-" will be replaced with "must be true"
     * @throws RuntimeException if argument does not match requested precondition
     */
    public static void isTrue(boolean bool, String message) {
        message = message.replaceAll("\\+-", "must be true");

        isTrue(bool, new NullPointerException(message));

    }

    /**
     * Makes sure given condition is true
     *
     * @param bool the condition
     * @param ex the exception to throw if the boolean is false
     * @throws RuntimeException ex if argument does not match requested precondition
     */
    public static void isTrue(boolean bool, RuntimeException ex) {
        if (!bool)
            throw ex == null ? new RuntimeException() : ex;


    }

    /**
     * Makes sure given condition is false
     *
     * @param bool the condition
     * @param ex   the exception to throw if the boolean is true
     * @throws RuntimeException ex if argument does not match requested precondition
     *
     */
    @ApiStatus.AvailableSince("0.0.39")
    public static void isFalse(boolean bool, RuntimeException ex) {
        if (bool)
            throw ex == null ? new RuntimeException() : ex;
    }


    /**
     * Makes sure given condition is false
     *
     * @param bool the condition
     * @throws RuntimeException if argument does not match requested precondition
     */
    @ApiStatus.AvailableSince("0.0.39")
    public static void isFalse(boolean bool) {
        isFalse(bool, new RuntimeException());
    }

    /**
     * Makes sure given condition is false
     *
     * @param bool the condition
     * @param message the message to send with the exception, "+-" will be replaced with "must be false"
     * @throws RuntimeException if argument does not match requested precondition
     *
     */
    @ApiStatus.AvailableSince("0.0.39")
    public static void isFalse(boolean bool, String message) {
        message = message.replaceAll("\\+-", "must be false");
        isFalse(bool, new RuntimeException(message));
    }

}
