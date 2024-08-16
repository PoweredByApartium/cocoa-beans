package net.apartium.cocoabeans.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListCollectionManagerTest {

    private ListTestingManager<String> listManager;

    @BeforeEach
    void setUp() {
        listManager = new ListTestingManager<>();
    }

    @Test
    void testAdd() {
        assertTrue(listManager.add("element1"));
        assertTrue(listManager.contains("element1"));
    }

    @Test
    void testRemove() {
        listManager.add("element2");
        assertTrue(listManager.remove("element2"));
        assertFalse(listManager.contains("element2"));
    }

    @Test
    void testContains() {
        listManager.add("element3");
        assertTrue(listManager.contains("element3"));
        assertFalse(listManager.contains("element4"));
    }

    @Test
    void testSize() {
        listManager.add("element5");
        assertEquals(1, listManager.size());
        listManager.add("element6");
        assertEquals(2, listManager.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(listManager.isEmpty());
        listManager.add("element7");
        assertFalse(listManager.isEmpty());
    }

    @Test
    void testClear() {
        listManager.add("element8");
        listManager.clear();
        assertTrue(listManager.isEmpty());
    }

    @Test
    void testAddAll() {
        List<String> elements = List.of("element9", "element10");
        assertTrue(listManager.addAll(elements));
        assertTrue(listManager.contains("element9"));
        assertTrue(listManager.contains("element10"));
    }

    @Test
    void testRemoveAll() {
        listManager.add("element11");
        listManager.add("element12");
        assertTrue(listManager.removeAll(List.of("element11")));
        assertFalse(listManager.contains("element11"));
        assertTrue(listManager.contains("element12"));
    }

    @Test
    void testRetainAll() {
        listManager.add("element13");
        listManager.add("element14");
        assertTrue(listManager.retainAll(List.of("element13")));
        assertTrue(listManager.contains("element13"));
        assertFalse(listManager.contains("element14"));
    }

    @Test
    void testIteratorEmpty() {
        Iterator<String> iterator = listManager.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorSingleElement() {
        listManager.add("element1");
        Iterator<String> iterator = listManager.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("element1", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorMultipleElements() {
        listManager.add("element1");
        listManager.add("element2");
        listManager.add("element3");

        Iterator<String> iterator = listManager.iterator();
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
        listManager.add("element1");
        listManager.add("element2");
        Iterator<String> iterator = listManager.iterator();

        assertTrue(iterator.hasNext());
        iterator.next();
        iterator.remove();

        assertFalse(listManager.contains("element1"));
        assertTrue(listManager.contains("element2"));
    }
}
