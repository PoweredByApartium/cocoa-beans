package net.apartium.cocoabeans.commands.virtual;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.multilayered.Permission;
import net.apartium.cocoabeans.commands.multilayered.PermissionFactory;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SimpleVirtualTest {

    @Test
    void createSimpleVirtualFromCommandNode() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        virtualCommandFactory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class);
            if (permission != null)
                metadata.put("permission", permission.value());
        });

        VirtualCommandDefinition virtualCommandDefinition = virtualCommandFactory.create(simpleCommand);

        assertNotNull(virtualCommandDefinition);
        assertEquals("simple", virtualCommandDefinition.name());
        assertEquals(Set.of(), virtualCommandDefinition.aliases());

        assertTrue(virtualCommandDefinition.info().getLongDescription().isEmpty());
        assertTrue(virtualCommandDefinition.info().getUsage().isEmpty());
        assertEquals(1, virtualCommandDefinition.info().getDescriptions().size());

        assertEquals("A simple description", virtualCommandDefinition.info().getDescriptions().get(0));

        assertEquals(3, virtualCommandDefinition.variants().size());
        assertEquals(Map.of("permission", "meow"), virtualCommandDefinition.metadata());

        VirtualCommandVariant setVariant = virtualCommandDefinition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("set <int>"))
                .findFirst()
                .orElseGet(Assertions::fail);

        VirtualCommandVariant clearVariant = virtualCommandDefinition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("clear"))
                .findFirst()
                .orElseGet(Assertions::fail);

        VirtualCommandVariant stringVariant = virtualCommandDefinition.variants()
                .stream()
                .filter(variant -> variant.variant().equals("<string>"))
                .findFirst()
                .orElseGet(Assertions::fail);

        assertNotNull(setVariant);
        assertNotNull(clearVariant);
        assertNotNull(stringVariant);

        // Set variant
        assertEquals(List.of(), setVariant.info().getDescriptions());
        assertEquals(List.of(), setVariant.info().getLongDescriptions());
        assertEquals(List.of(), setVariant.info().getUsages());

        assertEquals(Map.of(), setVariant.metadata());

        assertEquals("set <int>", setVariant.variant());
        // clear variant
        assertEquals(1, clearVariant.info().getDescriptions().size());
        assertEquals("Variant that clear stuff", clearVariant.info().getDescriptions().get(0));
        assertEquals(List.of(), clearVariant.info().getLongDescriptions());
        assertEquals(List.of(), clearVariant.info().getUsages());

        assertEquals(Map.of("permission", "my.permission"), clearVariant.metadata());

        assertEquals("clear", clearVariant.variant());
        // string variant
        assertEquals(List.of(), stringVariant.info().getDescriptions());
        assertEquals(List.of(), stringVariant.info().getLongDescriptions());
        assertEquals(List.of(), stringVariant.info().getUsages());

        assertEquals(Map.of(), stringVariant.metadata());

        assertEquals("<string>", stringVariant.variant());
    }

    @Test
    void simpleRegistrationVirtualCommand() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        virtualCommandFactory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class);
            if (permission != null)
                metadata.put("permission", permission.value());
        });

        VirtualCommandDefinition virtualCommandDefinition = virtualCommandFactory.create(simpleCommand);


        TestCommandManager commandManager = new TestCommandManager();
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        commandManager.addMetadataHandler(metadata -> {
            Set<Requirement> requirements = new HashSet<>();
            if (metadata.containsKey("permission"))
                requirements.add(new PermissionFactory.PermissionImpl(null, (String) metadata.get("permission")));

            return requirements;
        });

        commandManager.addVirtualCommand(virtualCommandDefinition, context -> {
            context.sender().sendMessage("You run: " + String.join(" ", context.args()));
            return true;
        });
    }

    @Test
    void jacksonTestVirtualCommand() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        virtualCommandFactory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class);
            if (permission != null)
                metadata.put("permission", permission.value());
        });

        VirtualCommandDefinition virtualCommandDefinition = virtualCommandFactory.create(simpleCommand);


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(virtualCommandDefinition);
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});

            assertTrue(areEqual(Map.of(
                    "name", "simple",
                    "aliases", List.of(),
                    "info", Map.of(
                            "descriptions", List.of("A simple description"),
                            "usages", List.of(),
                            "longDescriptions", List.of()
                    ),
                    "variants", List.of(
                            Map.of(
                                    "info", Map.of(
                                            "descriptions", List.of(),
                                            "usages", List.of(),
                                            "longDescriptions", List.of()
                                    ),
                                    "variant", "set <int>",
                                    "metadata", Map.of(),
                                    "ignoreCase", true
                            ),
                            Map.of(
                                    "info", Map.of(
                                            "descriptions", List.of("Variant that clear stuff"),
                                            "usages", List.of(),
                                            "longDescriptions", List.of()
                                    ),
                                    "variant", "clear",
                                    "metadata", Map.of(
                                            "permission", "my.permission"
                                    ),
                                    "ignoreCase", true
                            ),
                            Map.of(
                                    "info", Map.of(
                                            "descriptions", List.of(),
                                            "usages", List.of(),
                                            "longDescriptions", List.of()
                                    ),
                                    "variant", "<string>",
                                    "metadata", Map.of(),
                                    "ignoreCase", true
                            )
                    ),
                    "metadata", Map.of(
                            "permission", "meow"
                    )
            ), map));

            VirtualCommandDefinition other = objectMapper.readValue(json, VirtualCommandDefinition.class);
            assertEquals(virtualCommandDefinition, other);
        } catch (JsonProcessingException e) {
            fail("JSON processing failed: " + e.getMessage(), e);
        }
    }

    boolean areEqual(Map<String, Object> first, Map<String, Object> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> {
                    if (e.getValue() instanceof Map<?, ?> map)
                        return areEqual((Map<String, Object>) map, (Map<String, Object>) second.get(e.getKey()));

                    if (e.getValue() instanceof List<?> listA) {
                        List<?> listB = (List<?>) second.get(e.getKey());
                        if (listA.size() != listB.size())
                            return false;

                        Set<Object> setA = new HashSet<>(listA);
                        Set<Object> setB = new HashSet<>(listB);

                        Iterator<Object> iteratorA = setA.iterator();
                        Iterator<Object> iteratorB = setB.iterator();
                        while (iteratorA.hasNext() && iteratorB.hasNext()) {
                            Object a = iteratorA.next();
                            Object b = iteratorB.next();
                            if (a instanceof Map<?, ?> map && !areEqual((Map<String, Object>) map, (Map<String, Object>) b))
                                return false;

                            if (!a.equals(b))
                                return false;
                        }

                        return true;
                    }

                    return e.getValue().equals(second.get(e.getKey()));
                });
    }

    @Test
    void executeVirtualCommand() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        virtualCommandFactory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class);
            if (permission != null)
                metadata.put("permission", permission.value());
        });

        VirtualCommandDefinition virtualCommandDefinition = virtualCommandFactory.create(simpleCommand);

        TestCommandManager commandManager = new TestCommandManager();
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        commandManager.addMetadataHandler(metadata -> {
            Set<Requirement> requirements = new HashSet<>();
            if (metadata.containsKey("permission"))
                requirements.add(new PermissionFactory.PermissionImpl(null, (String) metadata.get("permission")));

            return requirements;
        });

        commandManager.addVirtualCommand(virtualCommandDefinition, context -> {
            context.sender().sendMessage("You run: " + String.join(" ", context.args()));
            return true;
        });

        TestSender sender = new TestSender();

        commandManager.handle(sender, "simple", new String[]{"test"});
        assertEquals(
                List.of("You run: test"),
                sender.getMessages()
        );

        sender.getMessages().clear();
        commandManager.handle(sender, "simple", new String[]{"one two three"});

        assertEquals(
                List.of("You run: one two three"),
                sender.getMessages()
        );
    }

    @Test
    void tabCompletionVirtualCommand() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        virtualCommandFactory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class);
            if (permission != null)
                metadata.put("permission", permission.value());
        });

        VirtualCommandDefinition virtualCommandDefinition = virtualCommandFactory.create(simpleCommand);


        TestCommandManager commandManager = new TestCommandManager();
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        commandManager.addMetadataHandler(metadata -> {
            Set<Requirement> requirements = new HashSet<>();
            if (metadata.containsKey("permission"))
                requirements.add(new PermissionFactory.PermissionImpl(null, (String) metadata.get("permission")));

            return requirements;
        });

        commandManager.addVirtualCommand(virtualCommandDefinition, context -> {
            context.sender().sendMessage("You run: " + String.join(" ", context.args()));
            return true;
        });

        TestSender sender = new TestSender();
        assertEquals(
                List.of(),
                commandManager.handleTabComplete(sender, "simple", new String[]{""})
                        .stream()
                        .sorted()
                        .toList()
        );

        sender.addPermission("meow");

        assertEquals(
                List.of("set"),
                commandManager.handleTabComplete(sender, "simple", new String[]{""})
                        .stream()
                        .sorted()
                        .toList()
        );

        sender.addPermission("my.permission");
        assertEquals(
                List.of("clear", "set"),
                commandManager.handleTabComplete(sender, "simple", new String[]{""})
                        .stream()
                        .sorted()
                        .toList()
        );

        assertEquals(
                List.of("clear"),
                commandManager.handleTabComplete(sender, "simple", new String[]{"c"})
                        .stream()
                        .sorted()
                        .toList()
        );

        assertEquals(
                List.of("set"),
                commandManager.handleTabComplete(sender, "simple", new String[]{"se"})
                        .stream()
                        .sorted()
                        .toList()
        );

        assertEquals(
                List.of("-", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
                commandManager.handleTabComplete(sender, "simple", new String[]{"set" , ""})
                        .stream()
                        .sorted()
                        .toList()
        );
    }

}
