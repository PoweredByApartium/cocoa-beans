package net.apartium.cocoabeans.spigot.lazies;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.spigot.SpigotTestBase;
import net.apartium.cocoabeans.spigot.test.commands.TestCommand;
import org.bukkit.command.Command;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CommandAutoRegistrationTest extends SpigotTestBase {

    @Override
    public void initialize() {

    }

    @Test
    void testRegisterLazyAssCommand() {
        new CommandAutoRegistration(this.plugin)
                .iAmALazyAssDeveloper()
                .register(TestCommand.class.getPackageName());

        Command command = this.server.getCommandMap().getCommand("test");
        assertNotNull(command);
        assertTrue(command.isRegistered());

        Command invalidCommand = this.server.getCommandMap().getCommand("missingImpl");
        assertNull(invalidCommand);

        Command devCommand = this.server.getCommandMap().getCommand("dev");
        assertNull(devCommand);

        Set<Command> pluginCommands = server.getCommandMap().getCommands()
                .stream()
                .filter(cmd -> cmd.getClass().isAnonymousClass())
                .collect(Collectors.toSet());

        assertEquals(2, pluginCommands.size());


    }

    @Test
    void commandDelegate() {
        new CommandAutoRegistration(this.plugin)
                .iAmALazyAssDeveloper()
                .register(TestCommand.class.getPackageName());

        Command command = this.server.getCommandMap().getCommand("test");
        assertNotNull(command);
        assertTrue(command.isRegistered());

        PlayerMock sender = server.addPlayer();
        assertTrue(server.dispatchCommand(sender, "test"));

        Command argsCommand = this.server.getCommandMap().getCommand("args");
        assertNotNull(argsCommand);
        assertTrue(argsCommand.isRegistered());

        assertFalse(server.dispatchCommand(sender, "args"));
        assertTrue(server.dispatchCommand(sender, "args test1"));
    }

    @Test
    void testRegisterDevCommands() {
        new CommandAutoRegistration(this.plugin)
                .iAmALazyAssDeveloper()
                .enableDevCommands(true)
                .register(TestCommand.class.getPackageName());

        Set<Command> pluginCommands = server.getCommandMap().getCommands()
                .stream()
                .filter(cmd -> cmd.getClass().isAnonymousClass())
                .collect(Collectors.toSet());

        assertEquals(3, pluginCommands.size());
    }

    @AfterEach
    @Override
    public void tearDown() {
        // don't do this at home kids
        server.getCommandMap().clearCommands();
        super.tearDown();
    }
}
