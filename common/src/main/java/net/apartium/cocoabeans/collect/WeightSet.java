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

/**
 * A set implementation with weighted keys
 * This class is not thread safe.
 * @param <E> element type
 */
public class WeightSet<E> implements Set<E> {

    private double totalWeight;
    private final Map<E, Double> elements;

    /**
     * Constructors an empty weight set instance
     */
    public WeightSet() {
        totalWeight = 0;
        elements = new HashMap<>();
    }

    /**
     * A copy constructor to create a new instance of WeightSet with same elements as given weight set
     * @param weightSet given weight set
     */
    public WeightSet(WeightSet<E> weightSet) {
        this.totalWeight = weightSet.totalWeight;
        this.elements = new HashMap<>(weightSet.elements);
    }

    /**
     * Construct a weight set instance from given map
     * @param map weight set base map, consisting of elements as keys and weight as values
     */
    public WeightSet(Map<? extends E, ? extends Double> map) {
        this.elements = new HashMap<>(map);
        this.totalWeight = calculateWeight();
    }

    /**
     * Insert an element with given weight into the set
     * @param element element to insert
     * @param weight weight of given element
     * @return optional consisting of weight previously associated with given element, empty if element was not in the set
     */
    public OptionalDouble put(E element, double weight) {
        if (weight < 0) throw new RuntimeException("Weight must be larger than 0");
        remove(element);
        Double oldWeight = elements.put(element, weight);
        this.totalWeight += weight;
        if (oldWeight == null || oldWeight.isNaN()) return OptionalDouble.empty();
        return OptionalDouble.of(oldWeight);
    }

    /**
     * Put all entries from given map into current instance.
     * You should prefer {@link WeightSet#putAll(WeightSet)} instead of this method.
     * @param map weight set base map, consisting of elements as keys and weight as values
     * @see WeightSet#putAll(WeightSet)
     */
    public void putAll(Map<? extends E, ? extends Double> map) {
        elements.putAll(map);
        this.totalWeight = calculateWeight();
    }

    /**
     * Put all entries from given weight set instance into the current instance
     * @param weightSet weight set to merge into the current one
     * @see WeightSet#putAll(Map)
     */
    public void putAll(WeightSet<E> weightSet) {
        this.elements.putAll(weightSet.elements);
        this.totalWeight = calculateWeight();
    }

    /**
     * {@inheritDoc}
     * @see Collection#clear()
     */
    @Override
    public void clear() {
        totalWeight = 0;
        elements.clear();
    }

    /**
     * Get weight associated with given element
     * @param element element
     * @return weight of given element, or empty if not in set
     * @see WeightSet#getWeightOrDefault(Object, double)
     */
    public OptionalDouble getWeight(E element) {
        Double val = elements.get(element);
        if (val == null || val.isNaN())
            return OptionalDouble.empty();

        return OptionalDouble.of(val);
    }

    /**
     * Get weight associated with given element
     * @param element element
     * @param defaultValue default weight to return if element is not in set
     * @return weight of given element, or given default value if element not in set
     * @see WeightSet#getWeight(Object)
     */
    public double getWeightOrDefault(E element, double defaultValue) {
        return elements.getOrDefault(element, defaultValue);
    }

    /**
     * Get weight of given element as percentage of total weight in weight set
     * @param element element
     * @return weight of given element as %, or empty if element is not in set
     */
    public OptionalDouble getPercentage(E element) {
        Double weight = elements.get(element);
        if (weight != null && !weight.isNaN()) {
            return OptionalDouble.of(weight / this.totalWeight * 100);
        }
        return OptionalDouble.empty();
    }

    /**
     * Get all elements in this weight set
     * @return unmodifiable set consisting of all elements of this set
     */
    public Set<E> values() {
        return Collections.unmodifiableSet(elements.keySet());
    }

    /**
     * Get total weight of all elements in set
     * @return total weight of all elements in set
     */
    public double totalWeight() {
        return totalWeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        Double value = elements.remove(o);
        if (value == null || value.isNaN()) return false;
        totalWeight -= value;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return elements.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return elements.containsKey(o);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Iterator<E> iterator() {
        return elements.keySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return elements.entrySet().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] ts) {
        return elements.keySet().toArray(ts);
    }

    /**
     * {@inheritDoc}
     * You should prefer {@link WeightSet#put(Object, double)} over this method, since you can specify weight with it
     */
    @Override
    public boolean add(E element) {
        return put(element, 0).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return elements.keySet().containsAll(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(@NotNull Collection<? extends E> collection) {
        if (collection instanceof WeightSet<? extends E> weightSet) {
            putAll((Map<? extends E, ? extends Double>) weightSet);
            return true;
        }

        for (E element : collection) put(element, 0);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return elements.keySet().removeIf(element -> !collection.contains(element));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        for (Object obj : collection) remove(obj);
        return true;
    }

    /**
     * Randomly pick one element from set based on their weight. 
     * The higher the weight the bigger chance it has of being the result. 
     * @return picked element, or null if list is empty
     * @see WeightSet#pickOne(Random) 
     */
    public E pickOne() {
        return pickOne(ThreadLocalRandom.current());
    }

    /**
     * Randomly pick one element from set based on their weight. 
     * The higher the weight the bigger chance it has of being the result. 
     * @param random random instance to provide random number
     * @return picked element, or null if list is empty
     * @see WeightSet#pickOne()
     */
    public E pickOne(Random random) {
        if (elements.size() == 0) return null;
        double target = random.nextDouble() * totalWeight;

        double count = 0;
        for (var entry : elements.entrySet()) {
            count += entry.getValue();
            if (count >= target) return entry.getKey();
        }

        return null;
    }

    /**
     * Randomly pick given number of elements from set based on their weight. 
     * The higher the weight the bigger chance it has of being in the resulting weightset. 
     * @param num number of elements to return
     * @return a subset of this weight set with random picks
     * @see WeightSet#pickMany(int, Random) 
     */
    public WeightSet<E> pickMany(int num) {
        return pickMany(num, ThreadLocalRandom.current());
    }

    /**
     * Randomly pick given number of elements from set based on their weight. 
     * The higher the weight the bigger chance it has of being in the resulting weightset. 
     * @param num number of elements to return
     * @param random random instance to provide random number
     * @return a subset of this weight set with random picks
     * @see WeightSet#pickMany(int) 
     */
    public WeightSet<E> pickMany(int num, Random random) {
        if (num <= 0) throw new RuntimeException("Number of elements must be bigger than 0");
        if (elements.size() < num) throw new RuntimeException("Number of elements must be smaller/equals elements size");

        if (elements.size() == num) return new WeightSet<>(this);
        WeightSet<E> result = new WeightSet<>();

        double total = totalWeight;
        List<E> list = new ArrayList<>(values());

        for (int i = 0; i < num; i++) {
            double count = 0;
            double target = random.nextDouble() * total;
            int index = 0;

            for (E key : list) {
                count += getWeight(key).orElse(0);
                if (count >= target) break;
                index++;
            }

            E element = list.remove(index);
            double weight = getWeight(element).orElse(0);
            total -= weight;
            result.put(element, weight);
        }

        return result;
    }

    private double calculateWeight() {
        double weight = 0;

        for (Double num : elements.values()) {
            if (num.isInfinite() || num.isNaN()) throw new RuntimeException("Can't be infinity or nan");
            weight += num;
        }

        return weight;
    }

}
