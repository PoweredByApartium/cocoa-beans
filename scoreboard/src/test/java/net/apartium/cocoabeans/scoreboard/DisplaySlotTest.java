package net.apartium.cocoabeans.scoreboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DisplaySlotTest {

    @Test
    void test() {
        assertEquals(0, DisplaySlot.LIST.getId());
        assertEquals(1, DisplaySlot.SIDEBAR.getId());
        assertEquals(2, DisplaySlot.BELOW_NAME.getId());
    }

}
