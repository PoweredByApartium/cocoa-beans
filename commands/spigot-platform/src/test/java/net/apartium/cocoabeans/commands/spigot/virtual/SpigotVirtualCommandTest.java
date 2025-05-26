package net.apartium.cocoabeans.commands.spigot.virtual;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import net.apartium.cocoabeans.commands.spigot.SpigotSender;
import net.apartium.cocoabeans.commands.spigot.requirements.Permission;
import net.apartium.cocoabeans.commands.spigot.requirements.factory.PermissionFactory;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandDefinition;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandFactory;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SpigotVirtualCommandTest extends CommandsSpigotTestBase {

    PlayerMock player;

    @BeforeEach
    void setupTestCommand() {
        VirtualCommandFactory factory = new VirtualCommandFactory();
        factory.addMetadataMapper((element, metadata) -> {
            Permission permission = element.getAnnotation(Permission.class);
            if (permission != null)
                metadata.put("permission", Map.of(
                        "value", permission.value(),
                        "invert", permission.invert()
                ));
        });

        PermissionFactory permissionFactory = new PermissionFactory();
        commandManager.addMetadataHandler(metadata -> {
            Set<Requirement> requirements = new HashSet<>();
            if (metadata.containsKey("permission")) {
                Requirement permission = permissionFactory.getRequirement(null, metadata.get("permission"));
                if (permission != null)
                    requirements.add(permission);
            }

            return requirements;
        });

        VirtualCommandDefinition virtualSimpleCommandDefinition = factory.create(new SimpleCommand());
        commandManager.addVirtualCommand(virtualSimpleCommandDefinition, context -> {
            context.sender().sendMessage("You run: " + String.join(" ", context.args()));
            return true;
        });

        player = server.addPlayer("ikfir");
    }

    @Test
    void handle() {
        player.performCommand("simple test");
        assertEquals("You run: test", player.nextMessage());

        player.performCommand("simple a b c");
        assertEquals("You run: a b c", player.nextMessage());
    }

    @Test
    void tabCompletion() {
        assertFalse(player.hasPermission("meow"));
        assertFalse(player.hasPermission("my.permission"));
        assertEquals(List.of(), handleTabCompletion(player, "simple", new String[]{""}));

        player.addAttachment(plugin, "meow", true);
        assertEquals(
                List.of("set"),
                handleTabCompletion(player, "simple", new String[]{""})
        );

        assertEquals(
                List.of("set"),
                handleTabCompletion(player, "simple", new String[]{"s"})
        );

        player.addAttachment(plugin, "my.permission", true);
        assertEquals(
                List.of("clear", "set"),
                handleTabCompletion(player, "simple", new String[]{""})
                        .stream()
                        .sorted()
                        .toList()
        );

        assertEquals(
                List.of("clear"),
                handleTabCompletion(player, "simple", new String[]{"c"})
                        .stream()
                        .sorted()
                        .toList()
        );

    }

    List<String> handleTabCompletion(CommandSender sender, String label, String[] args) {
        return commandManager.handleTabComplete(new SpigotSender<>(sender), label, args);
    }


}
