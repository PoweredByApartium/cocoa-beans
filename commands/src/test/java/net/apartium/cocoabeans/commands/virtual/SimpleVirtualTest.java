package net.apartium.cocoabeans.commands.virtual;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.multilayered.Permission;
import net.apartium.cocoabeans.commands.multilayered.PermissionFactory;
import net.apartium.cocoabeans.commands.requirements.RequirementOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SimpleVirtualTest {

    @Test
    void createSimpleVirtualFromCommandNode() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommand virtualCommand = VirtualCommand.create(simpleCommand);

        assertEquals("simple", virtualCommand.name());
        assertEquals(Set.of(), virtualCommand.aliases());

        assertTrue(virtualCommand.info().getLongDescription().isEmpty());
        assertTrue(virtualCommand.info().getUsage().isEmpty());
        assertEquals(1, virtualCommand.info().getDescriptions().size());

        assertEquals("A simple description", virtualCommand.info().getDescriptions().get(0));

        assertEquals(Set.of(new RequirementOption(Permission.class.getName(), Map.of("value", "meow"))), virtualCommand.requirements());

        assertEquals(3, virtualCommand.variants().size());

        CommandVariant setVariant = virtualCommand.variants()
                .stream()
                .filter(variant -> variant.variant().value().equals("set <int>"))
                .findFirst()
                .orElseGet(Assertions::fail);

        CommandVariant clearVariant = virtualCommand.variants()
                .stream()
                .filter(variant -> variant.variant().value().equals("clear"))
                .findFirst()
                .orElseGet(Assertions::fail);

        CommandVariant stringVariant = virtualCommand.variants()
                .stream()
                .filter(variant -> variant.variant().value().equals("<string>"))
                .findFirst()
                .orElseGet(Assertions::fail);

        assertNotNull(setVariant);
        assertNotNull(clearVariant);
        assertNotNull(stringVariant);

        // Set variant
        assertEquals(Set.of(), setVariant.requirements());

        assertEquals(List.of(), setVariant.info().getDescriptions());
        assertEquals(List.of(), setVariant.info().getLongDescriptions());
        assertEquals(List.of(), setVariant.info().getUsages());

        assertEquals("set <int>", setVariant.variant().value());
        // clear variant
        assertEquals(Set.of(), clearVariant.requirements());

        assertEquals(1, clearVariant.info().getDescriptions().size());
        assertEquals("Variant that clear stuff", clearVariant.info().getDescriptions().get(0));
        assertEquals(List.of(), clearVariant.info().getLongDescriptions());
        assertEquals(List.of(), clearVariant.info().getUsages());

        assertEquals("clear", clearVariant.variant().value());
        // string variant
        assertEquals(
                Set.of(new RequirementOption(Permission.class.getName(), Map.of("value","my.permission"))),
                stringVariant.requirements()
        );

        assertEquals(List.of(), stringVariant.info().getDescriptions());
        assertEquals(List.of(), stringVariant.info().getLongDescriptions());
        assertEquals(List.of(), stringVariant.info().getUsages());

        assertEquals("<string>", stringVariant.variant().value());
    }

    @Test
    void simpleRegistrationVirtualCommand() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommand virtualCommand = VirtualCommand.create(simpleCommand);

        TestCommandManager commandManager = new TestCommandManager();
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);

        commandManager.addVirtualCommand(virtualCommand, context -> {
            context.sender().sendMessage("You run: " + String.join(" ", context.args()));
            return true;
        });
    }

    @Test
    void jacksonTestVirtualCommand() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommand virtualCommand = VirtualCommand.create(simpleCommand);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writeValueAsString(virtualCommand));
        } catch (JsonProcessingException e) {
            System.out.println("oh no: ");
            e.printStackTrace();
        }
    }

    @Test
    void executeVirtualCommand() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommand virtualCommand = VirtualCommand.create(simpleCommand);

        TestCommandManager commandManager = new TestCommandManager();
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);

        commandManager.addVirtualCommand(virtualCommand, context -> {
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
        VirtualCommand virtualCommand = VirtualCommand.create(simpleCommand);

        TestCommandManager commandManager = new TestCommandManager();
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);

        commandManager.addVirtualCommand(virtualCommand, context -> {
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
