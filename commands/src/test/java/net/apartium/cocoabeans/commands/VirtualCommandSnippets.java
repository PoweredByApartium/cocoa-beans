package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.requirements.RequirementSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VirtualCommandSnippets {

    @Test
    void using() {
        SimpleCommand simpleCommand = new SimpleCommand();
        VirtualCommand virtualCommand = VirtualCommand.create(simpleCommand);

        TestCommandManager commandManager = new TestCommandManager();
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);

        commandManager.addVirtualCommand(
                virtualCommand,
                context -> {
                    context.sender().sendMessage("You run: " + String.join(" ", context.args()));
                    return true;
                },
                new RequirementSet(),
                Map.of()
        );

        TestSender sender = new TestSender();

        // Tab competition
        assertEquals(
                List.of("clear", "set"),
                commandManager.handleTabComplete(sender, "simple", new String[]{""})
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
                commandManager.handleTabComplete(sender, "simple", new String[]{"set", ""})
                        .stream()
                        .sorted()
                        .toList()
        );

        // Execute
        // Will always call callback
        commandManager.handle(sender, "simple", new String[]{"set 63"});
        assertEquals(
                List.of("You run: set 63"),
                sender.getMessages()
        );

        sender.getMessages().clear();

        commandManager.handle(sender, "simple", new String[]{"random variant that isn't exists"});
        assertEquals(
                List.of("You run: random variant that isn't exists"),
                sender.getMessages()
        );
    }


    @Command("simple")
    public class SimpleCommand implements CommandNode {

        @SubCommand("set <int>")
        public void set(Sender sender, int value) {
            // Do something
        }

        @Description("Variant that clear stuff")
        @SubCommand("clear")
        public void clear(Sender sender) {
            // Do something
        }

    }

}
