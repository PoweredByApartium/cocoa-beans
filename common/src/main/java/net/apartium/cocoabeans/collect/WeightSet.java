/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.collect;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WeightSet<E> implements Set<E> {

    private double weight;
    private final Map<E, Double> elements;

    public WeightSet() {
        weight = 0;
        elements = new HashMap<>();
    }

    public WeightSet(WeightSet<E> weightSet) {
        this.weight = weightSet.weight;
        this.elements = new HashMap<>(weightSet.elements);
    }

    public WeightSet(Map<? extends E, ? extends Double> map) {
        double weight = 0;

        for (Double num : map.values()) {
            if (num.isInfinite() || num.isNaN()) throw new RuntimeException("Can't be infinity or nan");
            weight += num;
        }

        this.weight = weight;
        this.elements = new HashMap<>(map);
    }

    public OptionalDouble put(E element, double weight) {
        if (weight < 0) throw new RuntimeException("Weight must be larger than 0");
        remove(element);
        Double oldWeight = elements.put(element, weight);
        this.weight += weight;
        if (oldWeight == null || oldWeight.isNaN()) return OptionalDouble.empty();
        return OptionalDouble.of(oldWeight);
    }

    public void putAll(Map<? extends E, ? extends Double> map) {
        for (var entry : map.entrySet()) put(entry.getKey(), entry.getValue());
    }

    public void clear() {
        weight = 0;
        elements.clear();
    }

    public double getWeight(E element) {
        return elements.get(element);
    }

    public double getWeightOrDefault(E element, double defaultValue) {
        return elements.getOrDefault(element, defaultValue);
    }


    public double getPercentage(E element) {
        return elements.get(element) / weight * 100;
    }

    public Set<E> values() {
        return Collections.unmodifiableSet(elements.keySet());
    }

    public double totalWeight() {
        return weight;
    }

    @Override
    public boolean remove(Object o) {
        Double value = elements.remove(o);
        if (value == null || value.isNaN()) return false;
        weight -= value;
        return true;
    }

    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return elements.containsKey(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return elements.keySet().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return elements.entrySet().toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        return elements.keySet().toArray(ts);
    }

    @Override
    public boolean add(E element) {
        return put(element, 0).isEmpty();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return elements.keySet().containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        if (collection instanceof WeightSet<? extends E> weightSet) {
            for (var entry : weightSet.elements.entrySet())
                put(entry.getKey(), entry.getValue());
            return true;
        }

        for (E element : collection) put(element, 0);
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        for (Object obj : collection) remove(obj);
        return true;
    }

    public E pickOne() {
        return pickOne(ThreadLocalRandom.current());
    }

    public E pickOne(Random random) {
        if (elements.size() == 0) return null;
        double target = random.nextDouble() * weight;

        double count = 0;
        for (var entry : elements.entrySet()) {
            count += entry.getValue();
            if (count >= target) return entry.getKey();
        }

        return null;
    }

    public WeightSet<E> pickMany(int num) {
        return pickMany(num, ThreadLocalRandom.current());
    }

    public WeightSet<E> pickMany(int num, Random random) {
        if (num <= 0) throw new RuntimeException("Number of elements must be bigger than 0");
        if (elements.size() < num) throw new RuntimeException("Number of elements must be smaller/equals elements size");

        if (elements.size() == num) return new WeightSet<>(this);
        WeightSet<E> result = new WeightSet<>();

        double total = weight;
        List<E> list = new ArrayList<>(values());


        for (int i = 0; i < num; i++) {
            double count = 0;
            double target = random.nextDouble() * total;
            int index = 0;

            for (E key : list) {
                count += getWeight(key);
                if (count >= target) break;
                index++;
            }

            E element = list.remove(index);
            total -= getWeight(element);
            result.put(element, getWeight(element));
        }

        return result;
    }


}
