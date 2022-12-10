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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A utility class providing "dummy" implementations of java collection api classes.
 * The provided objects are guaranteed to always remain empty. If a piece of code attempts to alter the state of the object, nothing will happen,
 * this is in contrast to java's own {@link Collections#emptyMap()} or {@link Collections#emptySet()}, which will throw an exception when something attempts to modify it.
 * It is useful for shimming and rigging external code without causing too much trouble.
 *
 * @author Voigon (Lior S.)
 */
public class Dummies {

    /**
     * Returns the dummy map instance.
     * @return the dummy map instance.
     * @param <K> map key type
     * @param <V> map value type
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> dummyMap() {
        return DummyMap.INSTANCE;
    }

    /**
     * Returns the dummy set instance
     * @return the dummy set instance
     * @param <E> element type
     */
    @SuppressWarnings("unchecked")
    public static <E> Set<E> dummySet() { return DummySet.INSTANCE; }
    
    private static final class DummySet<E> implements Set<E> {

        private static final DummySet INSTANCE = new DummySet<>();

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return Collections.emptyIterator();
        }

        @NotNull
        @Override
        public Object @NotNull [] toArray() {
            return new Object[0];
        }

        @NotNull
        @Override
        public <T> T @NotNull [] toArray(@NotNull T[] a) {
            return a;
        }

        @Override
        public boolean add(E e) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends E> c) {
            return false;
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    }

    private static final class DummyMap<K, V> implements Map<K, V> {

        private static final DummyMap INSTANCE = new DummyMap<>();
        
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public V get(Object key) {
            return null;
        }

        @Nullable
        @Override
        public V put(K key, V value) {
            return null;
        }

        @Override
        public V remove(Object key) {
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends K, ? extends V> m) {
            // nothing to do
        }

        @Override
        public void clear() {
            // nothing to do
        }

        @NotNull
        @Override
        public Set<K> keySet() {
            return dummySet();
        }

        @NotNull
        @Override
        public Collection<V> values() {
            return dummySet();
        }

        @NotNull
        @Override
        public Set<Entry<K, V>> entrySet() {
            return dummySet();
        }
    }

}
