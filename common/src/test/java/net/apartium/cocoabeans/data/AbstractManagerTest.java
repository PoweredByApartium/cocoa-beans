package net.apartium.cocoabeans.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class AbstractManagerTest {

    private TestingManager<String, Integer> manager;

    @BeforeEach
    void setUp() {
        manager = new TestingManager<>();
    }

    @Test
    void testPut() {
        manager.put("key1", 1);
        assertEquals(1, manager.get("key1").orElse(null));
    }

    @Test
    void testGet() {
        manager.put("key2", 2);
        assertEquals(Optional.of(2), manager.get("key2"));
    }

    @Test
    void testGetAbsentKey() {
        assertEquals(Optional.empty(), manager.get("absentKey"));
    }

    @Test
    void testRemove() {
        manager.put("key3", 3);
        assertEquals(3, manager.remove("key3"));
        assertEquals(Optional.empty(), manager.get("key3"));
    }

    @Test
    void testContainsKey() {
        manager.put("key4", 4);
        assertTrue(manager.containsKey("key4"));
        assertFalse(manager.containsKey("absentKey"));
    }

    @Test
    void testContainsValue() {
        manager.put("key5", 5);
        assertTrue(manager.containsValue(5));
        assertFalse(manager.containsValue(10));
    }

    @Test
    void testKeySet() {
        manager.put("key6", 6);
        Set<String> keys = manager.keySet();
        assertTrue(keys.contains("key6"));
    }

    @Test
    void testValues() {
        manager.put("key7", 7);
        assertTrue(manager.values().contains(7));
    }

    @Test
    void testEntrySet() {
        manager.put("key8", 8);
        Set<Map.Entry<String, Integer>> entries = manager.entrySet();
        assertTrue(entries.stream().anyMatch(entry -> entry.getKey().equals("key8") && entry.getValue().equals(8)));
    }

    @Test
    void testClear() {
        manager.put("key9", 9);
        manager.clear();
        assertTrue(manager.isEmpty());
    }

    @Test
    void testSize() {
        manager.put("key10", 10);
        manager.put("key11", 11);
        assertEquals(2, manager.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(manager.isEmpty());
        manager.put("key12", 12);
        assertFalse(manager.isEmpty());
    }

    @Test
    void testPutAll() {
        Map<String, Integer> map = new HashMap<>();
        map.put("key13", 13);
        map.put("key14", 14);
        manager.putAll(map);
        assertEquals(13, manager.get("key13").orElse(null));
        assertEquals(14, manager.get("key14").orElse(null));
    }

    @Test
    void testCompute() {
        manager.put("key15", 15);
        Integer result = manager.compute("key15", (k, v) -> v == null ? 1 : v + 1);
        assertEquals(16, result);
    }

    @Test
    void testMerge() {
        manager.put("key16", 16);
        Integer result = manager.merge("key16", 5, Integer::sum);
        assertEquals(21, result);
    }

    @Test
    void testReplace() {
        manager.put("key17", 17);
        boolean replaced = manager.replace("key17", 17, 18);
        assertTrue(replaced);
        assertEquals(18, manager.get("key17").orElse(null));
    }

    @Test
    void testReplaceWithValue() {
        manager.put("key18", 18);
        Integer previousValue = manager.replace("key18", 19);
        assertEquals(18, previousValue);
        assertEquals(19, manager.get("key18").orElse(null));
    }

    @Test
    void testGetOrDefault() {
        manager.put("key19", 19);
        assertEquals(19, manager.getOrDefault("key19", 100));
        assertEquals(100, manager.getOrDefault("absentKey", 100));
    }
}
