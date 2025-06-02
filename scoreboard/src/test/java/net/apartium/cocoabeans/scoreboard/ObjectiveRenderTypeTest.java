package net.apartium.cocoabeans.scoreboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjectiveRenderTypeTest {

    @Test
    void test() {
        assertEquals(0, ObjectiveRenderType.INTEGER.getId());
        assertEquals(1, ObjectiveRenderType.HEARTS.getId());
    }

}
