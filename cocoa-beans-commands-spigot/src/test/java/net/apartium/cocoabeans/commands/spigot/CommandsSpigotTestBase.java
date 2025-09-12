package net.apartium.cocoabeans.commands.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import net.apartium.cocoabeans.commands.CommandManager;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

public class CommandsSpigotTestBase {

    protected ServerMock server;

    protected MockPlugin plugin;

    protected SpigotCommandManager commandManager;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();

        commandManager = new SpigotCommandManager(plugin, createArgumentMapper());
        commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        commandManager.registerArgumentTypeHandler(SpigotCommandManager.SPIGOT_PARSERS);
    }

    protected SpigotArgumentMapper createArgumentMapper() {
        return new SpigotArgumentMapper();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        server = null;
        plugin = null;
        commandManager = null;
    }

    public void execute(CommandSender sender, String command) {
        String[] split = command.split("\\s+");
        execute(sender, split[0], Arrays.copyOfRange(split, 1, split.length));
    }

    public void execute(CommandSender sender, String commandName, String args) {
        try {
            commandManager.handle(new SpigotSender<>(sender), commandName, args.split("\\s+"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public void execute(CommandSender sender, String commandName, String[] args) {
        try {
            commandManager.handle(new SpigotSender<>(sender), commandName, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> evaluateTabCompletion(CommandSender sender, String label, String args) {
        return commandManager.handleTabComplete(new SpigotSender<>(sender), label, args.split("\\s+"));
    }

}
