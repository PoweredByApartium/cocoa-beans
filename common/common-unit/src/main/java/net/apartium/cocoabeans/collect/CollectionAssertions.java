package net.apartium.cocoabeans.collect;

import net.apartium.cocoabeans.CollectionHelpers;

import java.util.Iterator;
import java.util.List;

import static net.apartium.cocoabeans.CollectionHelpers.range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CollectionAssertions {

    public static void assertEqualList(List<?> expected, List<?> actual) {
        if (expected == actual)
            return;

        if (expected == null || actual == null) {
            fail("Expected: " + expected + ", actual: " + actual);
            return;
        }

        if (expected.size() != actual.size()) {
            fail("Expected: " + expected + ", actual: " + actual + "\n" + "Expected size: " + expected.size() + ", actual size: " + actual.size());
            return;
        }

        if (expected.isEmpty())
            return;

        assertEquals(expected.size(), actual.size());

        List<Integer> leftIndex = range(0, expected.size(), 1);

        forLoop: for (Object object : expected) {
            Iterator<Integer> iterator = leftIndex.iterator();
            while (iterator.hasNext()) {
                int index = iterator.next();
                if (object.equals(actual.get(index))) {
                    iterator.remove();
                    continue forLoop;
                }
            }

            fail("Expected: " + expected + ", actual: " + actual);
            return;
        }

        if (!leftIndex.isEmpty()) {
            fail("Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertNotEqualList(List<?> expected, List<?> actual) {
        if (expected == actual) {
            fail("Expected: " + expected + ", actual: " + actual + "\nShould not be equal");
            return;
        }

        if (expected == null || actual == null)
            return;

        if (expected.size() != actual.size())
            return;

        if (expected.isEmpty())
            return;

        if (CollectionHelpers.equalsList(expected, actual)) {
            fail("Expected: " + expected + ", actual: " + actual + "\nShould not be equal");
        }
    }

}
