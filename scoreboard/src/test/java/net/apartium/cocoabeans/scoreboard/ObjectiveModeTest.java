package net.apartium.cocoabeans.scoreboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectiveModeTest {

    @Test
    void test() {
        assertEquals(0, ObjectiveMode.CREATE.getId());
        assertEquals(1, ObjectiveMode.REMOVE.getId());
        assertEquals(2, ObjectiveMode.UPDATE.getId());
    }

}
