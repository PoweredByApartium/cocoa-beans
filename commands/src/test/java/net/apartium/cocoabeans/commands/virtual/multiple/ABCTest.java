package net.apartium.cocoabeans.commands.virtual.multiple;

import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.virtual.ExtendCommand;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandDefinition;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandFactory;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ABCTest {

    @Test
    void abcTest() {
        VirtualCommandFactory factory = new VirtualCommandFactory();

        VirtualCommandDefinition definition = factory.create(new ABCCommandForTest(), new ABCCommandSecondForTest());
        assertEquals("abc",  definition.name());
        assertEquals(Set.of("cba", "bca"), new HashSet<>(definition.aliases()));

        assertTrue(definition.info().getDescriptions().isEmpty());
        assertTrue(definition.info().getLongDescriptions().isEmpty());
        assertTrue(definition.info().getUsages().isEmpty());

        assertTrue(definition.metadata().isEmpty());

        assertEquals(2, definition.variants().size());

        VirtualCommandVariant testAVariant = definition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("test a"))
                .findFirst()
                .orElseGet(Assertions::fail);

        VirtualCommandVariant testBVariant = definition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("test b"))
                .findFirst()
                .orElseGet(Assertions::fail);

        assertNotNull(testAVariant);
        assertNotNull(testBVariant);

        // test a variant
        assertTrue(testAVariant.info().getDescriptions().isEmpty());
        assertTrue(testAVariant.info().getLongDescriptions().isEmpty());
        assertTrue(testAVariant.info().getUsages().isEmpty());

        assertTrue(testAVariant.metadata().isEmpty());
        assertTrue(testAVariant.ignoreCase());

        assertEquals("test a", testAVariant.variant());

        // test b variant
        assertTrue(testBVariant.info().getDescriptions().isEmpty());
        assertTrue(testBVariant.info().getLongDescriptions().isEmpty());
        assertTrue(testBVariant.info().getUsages().isEmpty());

        assertTrue(testBVariant.metadata().isEmpty());
        assertTrue(testBVariant.ignoreCase());

        assertEquals("test b", testBVariant.variant());
    }

    @Test
    void failingTest() {
        VirtualCommandFactory factory = new VirtualCommandFactory();
        assertNull(factory.create());

        assertThrows(IllegalArgumentException.class, () -> factory.create(new ABCCommandForTest(), new CommandNode() {}));
        assertThrows(IllegalArgumentException.class, () -> factory.create(new ABCCommandForTest(), new ExtendCommand()));
        assertThrows(IllegalArgumentException.class, () -> factory.create(new ABCCommandForTest(), new ABCCommandNotForTest()));
    }

}
