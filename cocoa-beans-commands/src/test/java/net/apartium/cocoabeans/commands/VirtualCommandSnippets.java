package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.multilayered.Permission;
import net.apartium.cocoabeans.commands.multilayered.PermissionFactory;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandDefinition;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandFactory;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VirtualCommandSnippets {

    @Test
    void virtualCommandFactory() {
        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();

        // Metadata mapper is for mapping requirement to metadata
        virtualCommandFactory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class); // Find permission
            if (permission != null)
                metadata.put("permission", permission.value());
        });
    }

    @Test
    void using() {
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

        commandManager.addVirtualCommand(
                virtualCommandDefinition,
                context -> {
                    context.sender().sendMessage("You run: " + String.join(" ", context.args()));
                    return true;
                }
        );

        TestSender sender = new TestSender();
        // Tab competition
        assertEquals(
                List.of(),
                commandManager.handleTabComplete(sender, "simple", new String[]{""})
        );

        sender.addPermission("my.permission1");
        assertEquals(
                List.of("set"),
                commandManager.handleTabComplete(sender, "simple", new String[]{""})
                        .stream()
                        .sorted()
                        .toList()
        );

        sender.addPermission("my.permission2");
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



    @Permission("my.permission1")
    @Command("simple")
    public class SimpleCommand implements CommandNode {

        @SubCommand("set <int>")
        public void set(Sender sender, int value) {
            // Do something
        }

        @Description("Variant that clear stuff")
        @Permission("my.permission2")
        @SubCommand("clear")
        public void clear(Sender sender) {
            // Do something
        }

    }
}
