package net.apartium.cocoabeans.commands.parsers.compound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class EvilCompoundParserTest {

    @Test
    void creatingInstance() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new EvilCompoundParser("evil", 0));
        assertThrowsExactly(UnsupportedOperationException.class, () -> new AnotherEvilCompoundParser("evil", 0));
    }

}
