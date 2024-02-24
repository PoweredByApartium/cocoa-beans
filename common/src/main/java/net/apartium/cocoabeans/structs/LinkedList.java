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

import net.apartium.cocoabeans.CollectionHelpers;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collector;

/**
 *
 * @param <E>
 */
// TODO rename
public class LinkedList<E> implements Collection<E> {

    /**
     * Collect a stream into a linked list
     * @return a collector
     * @param <E> element type
     */
    public static <E> Collector<E, ?, LinkedList<E>> toLinkedList() {
        return Collector.of(
                LinkedList::new,
                LinkedList::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }
        );
    }

    /**
     * Construct a new instance with values consisting of specified range
     * @param start range start
     * @param end range end
     * @param step amount to step each time
     * @return a new linked list instance
     */
    public static LinkedList<Integer> range(int start, int end, int step) {
        if (step == 0) throw new IllegalArgumentException("Step cannot be zero");
        if (step < 0) throw new IllegalArgumentException("Step must be positive");

        boolean startBigger = start > end;
        if (startBigger) step = -step;

        LinkedList<Integer> result = new LinkedList<>();
        for (int i = start; (startBigger ? i > end : i < end); i += step) {
            result.add(i);
        }

        return result;
    }

    /**
     * Construct a linked list with given values
     * @param values values to add
     * @return a new linked list instance
     * @param <E> element type
     */
    @SafeVarargs
    public static <E> LinkedList<E> of(E... values) {
        LinkedList<E> result = new LinkedList<>();
        Collections.addAll(result, values);
        return result;
    }

    private int size;
    private Node<E> head;
    private Node<E> tail;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public boolean contains(Object o) {
        for (E value : this)
            if (value.equals(o))
                return true;

        return false;
    }

    public Optional<E> peek() {
        if (head == null)
            return Optional.empty();

        return Optional.of(head.getValue());
    }

    public Optional<E> pop() {
        if (head == null)
            return Optional.empty();

        E value = head.getValue();
        head = head.getNext();
        if (head == null)
            tail = null;

        size -= 1;
        return Optional.of(value);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        if (head == null)
            return Collections.emptyIterator();

        return new Iterator<E>() {

            private Node<E> current = LinkedList.this.head;
            private Node<E> prev = null;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                E value = current.getValue();
                prev = current;
                current = current.getNext();
                return value;
            }

            @Override
            public void remove() {
                if (LinkedList.this.size == 0) throw new RuntimeException("There is nothing to remove");
                if (prev == null) throw new RuntimeException("You didn't even iterator");
                if (LinkedList.this.size == 1) {
                    clear();
                    current = null;
                    return;
                }

                if (prev == head) {
                    LinkedList.this.head = current;
                    prev = current;
                    size -= 1;
                    return;
                }

                size -= 1;
                if (prev.getNext() == tail) tail = prev;
                prev.setNext(current);
            }
        };
    }

    @NotNull
    @Override
    public Object[] toArray() {
        if (head == null)
            return new Object[0];

        Object[] objects = new Object[size];
        Node<E> current = head;
        for (int i = 0; i < size && current != null; i++) {
            objects[i] = current.getValue();
            current = current.getNext();
        }
        return objects;
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] t1s) {
        Object[] result = (Object[]) Array.newInstance(t1s.getClass().getComponentType(), size());

        int i = 0;
        for (E e : this) {
            result[i++] = (T1) e;
        }

        return (T1[]) result;
    }
    @Override
    public boolean add(E value) {
        if (head == null) {
            head = new Node<>(value);
            tail = head;
        } else {
            tail.setNext(new Node<>(value));
            tail = tail.getNext();
        }
        size += 1;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return remove0(o, false);
    }

    boolean remove0(Object o, boolean all) {
        Iterator<E> iterator = this.iterator();
        boolean result = false;
        while (iterator.hasNext()) {
            E value = iterator.next();
            if (o.equals(value)) {
                iterator.remove();
                result = true;
                if (!all)
                    return true;

            }
        }

        return result;

    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return collection.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        for (E e : collection)
            add(e);

        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        boolean result = false;

        for (Object value : collection) {
            if (remove0(value, true)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        size = 0;
        head = null;
        tail = null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true; // quick check;

        if (!(o instanceof Collection<?> collection)) {
            return false;
        }

        if (size() != collection.size())
            return false;

        return CollectionHelpers.equalsArray(this.toArray(), collection.toArray());
    }

    @Override
    public String toString() {
        if (head == null)
            return "";

        return head.toString();
    }
}
