package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.multilayered.Permission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExtendCommandVirtualTest {

    @Test
    void createSimpleVirtualCommand() {
        VirtualCommandFactory factory = new VirtualCommandFactory();
        factory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class);
            if (permission != null)
                metadata.put("permission", permission.value());
        });

        VirtualCommandDefinition definition = factory.create(new ExtendCommand());
        assertEquals("extend", definition.name());
        assertEquals(Set.of(), definition.aliases());

        assertTrue(definition.info().getDescriptions().isEmpty());
        assertTrue(definition.info().getLongDescriptions().isEmpty());
        assertTrue(definition.info().getUsages().isEmpty());

        assertEquals(Map.of("test", "wow"), definition.metadata());

        assertEquals(4, definition.variants().size());

        VirtualCommandVariant setVariant = definition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("set <int>"))
                .findFirst()
                .orElseGet(Assertions::fail);

        VirtualCommandVariant clearVariant = definition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("clear"))
                .findFirst()
                .orElseGet(Assertions::fail);

        VirtualCommandVariant getVariant = definition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("get"))
                .findFirst()
                .orElseGet(Assertions::fail);


        VirtualCommandVariant removeVariant = definition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("remove <int>"))
                .findFirst()
                .orElseGet(Assertions::fail);

        assertNotNull(setVariant);
        assertNotNull(clearVariant);
        assertNotNull(getVariant);
        assertNotNull(removeVariant);

        // Set variant
        assertEquals(List.of(), setVariant.info().getDescriptions());
        assertEquals(List.of(), setVariant.info().getLongDescriptions());
        assertEquals(List.of(), setVariant.info().getUsages());

        assertEquals(Map.of("permission", "set.base"), setVariant.metadata());
        assertTrue(setVariant.ignoreCase());

        assertEquals("set <int>", setVariant.variant());
        // clear variant
        assertEquals(List.of(), clearVariant.info().getDescriptions());
        assertEquals(List.of(), clearVariant.info().getLongDescriptions());
        assertEquals(List.of(), clearVariant.info().getUsages());

        assertEquals(Map.of(), clearVariant.metadata());
        assertTrue(clearVariant.ignoreCase());

        assertEquals("clear", clearVariant.variant());
        // get variant
        assertEquals(List.of("Get info"), getVariant.info().getDescriptions());
        assertEquals(List.of(), getVariant.info().getLongDescriptions());
        assertEquals(List.of(), getVariant.info().getUsages());

        assertEquals(Map.of(), getVariant.metadata());
        assertTrue(getVariant.ignoreCase());

        assertEquals("get", getVariant.variant());
        // remove variant
        assertEquals(List.of(), removeVariant.info().getDescriptions());
        assertEquals(List.of(), removeVariant.info().getLongDescriptions());
        assertEquals(List.of(), removeVariant.info().getUsages());

        assertEquals(Map.of(), removeVariant.metadata());
        assertTrue(removeVariant.ignoreCase());

        assertEquals("remove <int>", removeVariant.variant());
    }

}
