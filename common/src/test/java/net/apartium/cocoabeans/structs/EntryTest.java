package net.apartium.cocoabeans.structs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    @Test
    void equals() {
        Entry<String, Integer> entry1 = new Entry<>("test", 1);
        Entry<String, Integer> entry2 = new Entry<>("test", 1);

        assertEquals(entry1, entry2);
    }

    @Test
    void identityEq() {
        Entry<String, Integer> entry1 = new Entry<>("test", 1);

        assertEquals(entry1, entry1);
    }

    @Test
    void notEquals() {
        Entry<String, Integer> entry1 = new Entry<>("test", 1);
        Entry<String, Integer> entry2 = new Entry<>("test1", 1);

        assertNotEquals(entry1, entry2);
    }

    @Test
    void notEqualsDifferentType() {
        Entry<String, Integer> entry1 = new Entry<>("test", 1);

        assertNotEquals(entry1, new Object());

    }

    @Test
    void testHashCode() {
        Entry<String, Integer> entry1 = new Entry<>("test", 1);
        Entry<String, Integer> entry2 = new Entry<>("test", 1);

        assertEquals(entry1.hashCode(), entry2.hashCode());
    }
}
