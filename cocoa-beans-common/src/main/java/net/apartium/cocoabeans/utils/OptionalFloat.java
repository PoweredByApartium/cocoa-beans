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

import net.apartium.cocoabeans.functions.FloatConsumer;
import net.apartium.cocoabeans.functions.FloatPredicate;
import net.apartium.cocoabeans.functions.FloatSupplier;
import net.apartium.cocoabeans.functions.FloatToObject;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A java optional like wrapper class for float primitive type
 * @see java.util.OptionalInt
 * @see Optional
 * @author Voigon
 */
public final class OptionalFloat {

    private static final OptionalFloat EMPTY = new OptionalFloat(0);

    /**
     * Retrieves a global empty OptionalFloat instance
     * @see Optional#empty()
     * @return  a global empty OptionalFloat instance
     */
    public static OptionalFloat empty() {
        return EMPTY;
    }

    /**
     * Creates a new OptionalFloat instance consisting of given value
     * @param val value
     * @return a new OptionalFloat instance
     */
    public static OptionalFloat of(float val) {
        return new OptionalFloat(val);
    }

    final float value;

    private OptionalFloat(float value) {
        this.value = value;
    }

    /**
     * Checks whether current instance is empty.
     * @return true if empty, otherwise false
     */
    public boolean isEmpty() {
        return this == EMPTY;
    }

    /**
     * Retrieves current value as float, or throws an exception if empty
     * @return current float value
     */
    public float getAsFloat() {
        if (isEmpty())
            throw new NoSuchElementException("No value present");

        return this.value;
    }

    /**
     * Executes given consumer if current instance is not empty
     * @param consumer given consumer action
     */
    public void ifPresent(FloatConsumer consumer) {
        if (!isEmpty())
            consumer.accept(this.value);

    }

    /**
     * Retrieves current value, or throws given exception if nul
     * @param exceptionSupplier exception supplier
     * @return float value
     * @throws RuntimeException exceptionSupplier if empty
     */
    public float getOrThrow(Supplier<RuntimeException> exceptionSupplier) {
        if (isEmpty())
            throw exceptionSupplier.get();
        return this.value;
    }

    /**
     * Filters given instance. Given predicate will not invoke if current instance is empty
     * @see Optional#filter(Predicate)
     * @param predicate if given predicate returns true, return the current instance, if false returns empty instance
     * @return
     */
    public OptionalFloat filter(FloatPredicate predicate) {
        if (isEmpty())
            return this;
        else
            return predicate.test(this.value) ? this : empty();
    }

    /**
     * Maps current value to an object. If empty, will return empty optional instance.
     * @param operation mapping operation
     * @return mapped value
     * @param <E> object element type
     */
    public <E> Optional<E> mapToObj(FloatToObject<E> operation) {
        return isEmpty() ? Optional.empty() : Optional.ofNullable(operation.map(this.value));
    }

    /**
     * Returns current value, or given value if empty
     * @param f value to return if current value is empty
     * @return current value, or given value if empty
     */
    public float orElse(float f) {
        return isEmpty() ? f : this.value;
    }

    /**
     * Attempts to return current value, but if this instance is empty retrieves value from given supplier
     * @param supplier supplier to be used if current instance is empty
     * @return float value
     */
    public float orElseGet(FloatSupplier supplier) {
        return isEmpty() ? supplier.getAsFloat() : this.value;
    }

    /**
     * @return true if current instance is not empty, else false
     */
    public boolean isPresent() {
        return !isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionalFloat that = (OptionalFloat) o;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return !isEmpty() ? String.format("Optional[%s]", value) : "Optional.empty";
    }

}
