package net.apartium.cocoabeans.collect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.apartium.cocoabeans.collect.CollectionAssertions.assertEqualList;
import static net.apartium.cocoabeans.collect.CollectionAssertions.assertNotEqualList;

public class CollectionAssertionsTest {

    @Test
    public void equalListTest() {
        assertEqualList(List.of(1, 2, 3), List.of(1, 2, 3));
        assertEqualList(List.of(), List.of());
        assertEqualList(null, null);
        assertEqualList(List.of(9, 3, 2), List.of(2, 9, 3));
        assertEqualList(List.of(83, 21, 75, 34, 12, 67), List.of(21, 83, 34, 75, 67, 12));

        assertNotEqualList(List.of(3, 2, 1), List.of(1, 2, 4));
        assertNotEqualList(List.of(3, 2, 1), null);
        assertNotEqualList(List.of(67, 21, 42, 312), List.of(32, 12, 36, 67));

        assertNotEqualList(null, List.of(1, 2, 3));
    }

}
