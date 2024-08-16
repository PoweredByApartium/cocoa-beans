package net.apartium.cocoabeans.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SetCollectionManagerTest {

    private SetTestingManager<String> setManager;

    @BeforeEach
    void setUp() {
        setManager = new SetTestingManager<>();
    }

    @Test
    void testAdd() {
        assertTrue(setManager.add("element1"));
        assertTrue(setManager.contains("element1"));
    }

    @Test
    void testRemove() {
        setManager.add("element2");
        assertTrue(setManager.remove("element2"));
        assertFalse(setManager.contains("element2"));
    }

    @Test
    void testContains() {
        setManager.add("element3");
        assertTrue(setManager.contains("element3"));
        assertFalse(setManager.contains("element4"));
    }

    @Test
    void testSize() {
        setManager.add("element5");
        assertEquals(1, setManager.size());
        setManager.add("element6");
        assertEquals(2, setManager.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(setManager.isEmpty());
        setManager.add("element7");
        assertFalse(setManager.isEmpty());
    }

    @Test
    void testClear() {
        setManager.add("element8");
        setManager.clear();
        assertTrue(setManager.isEmpty());
    }

    @Test
    void testAddAll() {
        Set<String> elements = Set.of("element9", "element10");
        assertTrue(setManager.addAll(elements));
        assertTrue(setManager.contains("element9"));
        assertTrue(setManager.contains("element10"));
    }

    @Test
    void testRemoveAll() {
        setManager.add("element11");
        setManager.add("element12");
        assertTrue(setManager.removeAll(Set.of("element11")));
        assertFalse(setManager.contains("element11"));
        assertTrue(setManager.contains("element12"));
    }

    @Test
    void testRetainAll() {
        setManager.add("element13");
        setManager.add("element14");
        assertTrue(setManager.retainAll(Set.of("element13")));
        assertTrue(setManager.contains("element13"));
        assertFalse(setManager.contains("element14"));
    }

    @Test
    void testIteratorEmpty() {
        Iterator<String> iterator = setManager.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorSingleElement() {
        setManager.add("element1");
        Iterator<String> iterator = setManager.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("element1", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorMultipleElements() {
        setManager.add("element1");
        setManager.add("element2");
        setManager.add("element3");

        Iterator<String> iterator = setManager.iterator();
        List<String> elements = new ArrayList<>();
        while (iterator.hasNext()) {
            elements.add(iterator.next());
        }
        assertTrue(elements.contains("element1"));
        assertTrue(elements.contains("element2"));
        assertTrue(elements.contains("element3"));
        assertEquals(3, elements.size());
    }

    @Test
    void testIteratorRemove() {
        setManager.add("element1");
        setManager.add("element2");
        Iterator<String> iterator = setManager.iterator();

        assertTrue(iterator.hasNext());
        iterator.next();
        iterator.remove();

        assertFalse(setManager.contains("element1"));
        assertTrue(setManager.contains("element2"));
    }
}
