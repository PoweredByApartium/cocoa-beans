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

import net.apartium.cocoabeans.Ensures;

import java.util.*;

public class WeightSet<T> {

    private static final Random RANDOM = new Random();

    private double weight;
    private final Map<T, Double> elements;

    public WeightSet() {
        weight = 0;
        elements = new HashMap<>();
    }

    public void put(T element, double weight) {
        if (weight < 0) throw new RuntimeException("Weight must be larger than 0");
        remove(element);
        elements.put(element, weight);
        this.weight += weight;
    }

    public void clear() {
        weight = 0;
        elements.clear();
    }

    public double getWeight(T element) {
        return elements.get(element);
    }

    public double getWeightOrDefault(T element, double defaultValue) {
        return elements.getOrDefault(element, defaultValue);
    }

    public boolean contains(T element) {
        return elements.containsKey(element);
    }

    public double getPercentage(T element) {
        return elements.get(element) / weight * 100;
    }

    public Set<T> values() {
        return elements.keySet();
    }

    public double totalWeight() {
        return weight;
    }

    public void remove(T element) {
        weight -= elements.getOrDefault(element, 0.0);
    }

    public T pickOne() {
        return pickOne(RANDOM);
    }

    public T pickOne(Random random) {
        if (elements.size() == 0) return null;
        double target = random.nextDouble() * weight;

        double count = 0;
        for (var entry : elements.entrySet()) {
            count += entry.getValue();
            if (count >= target) return entry.getKey();
        }

        return null;
    }

    public WeightSet<T> pickMany(int num) {
        return pickMany(num, RANDOM);
    }

    public WeightSet<T> pickMany(int num, Random random) {
        if (num <= 0) throw new RuntimeException("Number of elements must be bigger than 0");
        if (elements.size() < num) throw new RuntimeException("Number of elements must be smaller/equals elements size");

        if (elements.size() == num) {
            try {
                return (WeightSet<T>) clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        WeightSet<T> result = new WeightSet<>();

        double total = weight;
        List<T> list = new ArrayList<>(values());


        for (int i = 0; i < num; i++) {
            double count = 0;
            double target = random.nextDouble() * total;
            int index = 0;

            for (T key : list) {
                count += getWeight(key);
                if (count >= target) break;
                index++;
            }

            T element = list.remove(index);
            total -= getWeight(element);
            result.put(element, getWeight(element));
        }

        return result;
    }


}
