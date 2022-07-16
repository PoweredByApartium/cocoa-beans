/*
 * Copyright 2022 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.collect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import net.apartium.cocoabeans.functions.ByteConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * A basic, immutable list-like object which does not make use of the wrapper type to function.
 * @see java.util.List
 * @see Byte
 *
 * @author VOigon
 */
public class ImmutableByteArrayList {

    private static final ImmutableByteArrayList EMPTY = new ImmutableByteArrayList(new byte[0]);

    /**
     * Returns an empty instance.
     * @return empty instance
     */
    public static ImmutableByteArrayList of() {
        return EMPTY;
    }

    /**
     * Constructs a new instance of ImmutableByteArrayList with given values
     * @param values given values
     * @return a new instance consisting of given values in array
     */
    public static ImmutableByteArrayList of(byte... values) {
        byte[] content = Arrays.copyOf(values, values.length);
        return new ImmutableByteArrayList(content);
    }

    /**
     * Unwrap Byte collection as this type
     * @param c collection
     * @return a new instance consisting of given values in collection
     */
    public static ImmutableByteArrayList makeBetter(Collection<Byte> c) {
        Byte[] orig = c.toArray(new Byte[0]);
        byte[] content = new byte[c.size()];
        for (int i = 0; i < c.size(); i++)
            content[i] = orig[i];

        return new ImmutableByteArrayList(content);
    }

    final byte[] content;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private ImmutableByteArrayList(byte[] content) {
        this.content = content;
    }

    /**
     * @see Collection#size()
     */
    public int size() {
        return content.length;
    }

    /**
     * @see Collection#isEmpty()
     */
    public boolean isEmpty() {
        return content.length == 0;
    }

    /**
     * @see Collection#contains(Object)
     */
    public boolean contains(byte value) {
        for (byte element : content)
            if (value == element)
                return true;

        return false;
    }

    /**
     * @see Collection#iterator()
     */
    @NotNull
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            int position;

            @Override
            public boolean hasNext() {
                return position < content.length;
            }

            @Override
            public Byte next() {
                return content[++position];
            }
        };
    }

    /**
     * @see Collection#forEach(Consumer)
     */
    public void forEach(ByteConsumer consumer) {
        for (byte val : content)
            consumer.accept(val);

    }

    /**
     * @see Collection#stream()
     */
    public IntStream stream() {
        var builder = IntStream.builder();
        forEach(builder::add);
        return builder.build();
    }

    /**
     * @see Collection#toArray()
     */
    @JsonValue
    public byte[] toArray() {
        return Arrays.copyOf(this.content, this.content.length);
    }

    /**
     * @see java.util.List#get(int)
     */
    public byte get(int index) {
        return content[index];
    }

    /**
     * @see java.util.List#indexOf(Object)
     */
    public int indexOf(byte value) {
        for (int i = 0; i < content.length; i++) {
            byte val = content[i];
            if (val == value)
                return i;
        }
        return -1;
    }

    /**
     * @see java.util.List#lastIndexOf(Object)
     */
    public int lastIndexOf(byte value) {
        int result = -1;

        for (int i = 0; i < content.length; i++) {
            byte val = content[i];
            if (val == value)
                result = i;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableByteArrayList that = (ImmutableByteArrayList) o;
        return Arrays.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(content);
    }

    @Override
    public String toString() {
        return "ImmutableByteArrayList{" +
                "content=" + Arrays.toString(content) +
                '}';
    }

}
