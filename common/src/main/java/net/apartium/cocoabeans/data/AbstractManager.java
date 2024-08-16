package net.apartium.cocoabeans.data;

import java.util.*;
import java.util.function.BiFunction;

/**
 * An abstract manager class that provides common map operations.
 *
 * @param <K> the type of keys maintained by this manager
 * @param <V> the type of values maintained by this manager
 */
public abstract class AbstractManager<K, V> {

    protected final Map<K, V> map = new HashMap<>();

    /**
     * Associates the specified value with the specified key in this manager.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    public void put(K key, V value) {
        this.map.put(key, value);
    }

    /**
     * Returns {@link Optional} the value to which the specified key is mapped, or {@code null} if this manager contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this manager contains no mapping for the key
     */
    public Optional<V> get(K key) {
        return Optional.ofNullable(this.map.get(key));
    }

    /**
     * Removes the mapping for a key from this manager if it is present.
     *
     * @param key the key whose mapping is to be removed from the manager
     * @return the previous value associated with key, or {@code null} if there was no mapping for key
     */
    public V remove(K key) {
        return this.map.remove(key);
    }

    /**
     * Returns {@code true} if this manager contains a mapping for the specified key.
     *
     * @param key the key whose presence in this manager is to be tested
     * @return {@code true} if this manager contains a mapping for the specified key
     */
    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    /**
     * Returns {@code true} if this manager maps one or more keys to the specified value.
     *
     * @param value the value whose presence in this manager is to be tested
     * @return {@code true} if this manager maps one or more keys to the specified value
     */
    public boolean containsValue(V value) {
        return this.map.containsValue(value);
    }

    /**
     * Returns a {@link Set} view of the keys contained in this manager.
     *
     * @return a set view of the keys contained in this manager
     */
    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    /**
     * Returns a {@link Collection} view of the values contained in this manager.
     *
     * @return a collection view of the values contained in this manager
     */
    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this manager.
     *
     * @return a set view of the mappings contained in this manager
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }

    /**
     * Removes all mappings from this manager.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * Returns the number of key-value pairs in this manager.
     *
     * @return the number of key-value pairs in this manager
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Returns {@code true} if this manager contains no key-value pairs.
     *
     * @return {@code true} if this manager contains no key-value pairs
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Copies all of the mappings from the specified map to this manager.
     *
     * @param m mappings to be stored in this manager
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        this.map.putAll(m);
    }

    /**
     * Computes a value for the specified key using the provided remapping function.
     *
     * @param key              the key for which a value should be computed
     * @param remappingFunction the function used to compute the value
     * @return the computed value
     */
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.map.compute(key, remappingFunction);
    }

    /**
     * Merges the specified value with the existing value for the specified key using the provided remapping function.
     *
     * @param key               the key with which the specified value should be merged
     * @param value             the value to merge
     * @param remappingFunction the function used to merge the values
     * @return the merged value
     */
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.map.merge(key, value, remappingFunction);
    }

    /**
     * Replaces the entry for the specified key only if it is currently mapped to the specified value.
     *
     * @param key      the key with which the specified value is associated
     * @param oldValue the value expected to be associated with the specified key
     * @param newValue the new value to be associated with the specified key
     * @return {@code true} if the value was replaced
     */
    public boolean replace(K key, V oldValue, V newValue) {
        return this.map.replace(key, oldValue, newValue);
    }

    /**
     * Replaces the value for the specified key with the specified value.
     *
     * @param key   the key for which the value should be replaced
     * @param value the new value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no mapping for the key
     */
    public V replace(K key, V value) {
        return this.map.replace(key, value);
    }

    /**
     * Returns the value to which the specified key is mapped, or the specified default value if this manager contains no mapping for the key.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the value to return if this manager contains no mapping for the key
     * @return the value to which the specified key is mapped, or the specified default value if this manager contains no mapping for the key
     */
    public V getOrDefault(K key, V defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }
}
