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

import net.apartium.cocoabeans.functions.FloatConsumer;
import net.apartium.cocoabeans.functions.FloatPredicate;
import net.apartium.cocoabeans.functions.FloatSupplier;
import net.apartium.cocoabeans.functions.FloatToObject;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class OptionalFloat {

    private static final OptionalFloat EMPTY = new OptionalFloat(0);

    public static OptionalFloat empty() {
        return EMPTY;
    }

    public static OptionalFloat of(float val) {
        return new OptionalFloat(val);
    }

    final float value;

    private OptionalFloat(float value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public float getAsFloat() {
        if (isEmpty())
            throw new NoSuchElementException("No value present");

        return this.value;
    }

    public void ifPresent(FloatConsumer consumer) {
        if (!isEmpty())
            consumer.accept(this.value);

    }

    public float getOrThrow(Supplier<RuntimeException> exceptionSupplier) {
        if (isEmpty())
            throw exceptionSupplier.get();
        return this.value;
    }

    public OptionalFloat filter(FloatPredicate predicate) {
        if (isEmpty())
            return this;
        else
            return predicate.test(this.value) ? this : empty();
    }

    public <E> Optional<E> mapToObj(FloatToObject<E> operation) {
        return isEmpty() ? Optional.empty() : Optional.ofNullable(operation.map(this.value));
    }

    public float orElse(float f) {
        return isEmpty() ? f : this.value;
    }

    public float orElseGet(FloatSupplier supplier) {
        return isEmpty() ? supplier.getAsFloat() : this.value;
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

    public boolean isPresent() {
        return !isEmpty();
    }

}
