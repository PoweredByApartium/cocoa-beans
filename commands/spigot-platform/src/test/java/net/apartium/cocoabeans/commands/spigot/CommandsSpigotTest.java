package net.apartium.cocoabeans.commands.spigot;

import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.spigot.game.Stage;
import net.apartium.cocoabeans.commands.spigot.game.commands.GameCommand;
import net.apartium.cocoabeans.commands.spigot.game.commands.utils.NotInGameException;
import net.apartium.cocoabeans.commands.spigot.parsers.LocationParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommandsSpigotTest extends CommandsSpigotTestBase {

    PlayerMock ikfir;

    @BeforeEach
    void setupTestCommand() {
        commandManager.registerArgumentTypeHandler(new LocationParser(0));

        commandManager.addCommand(new CommandForTest());
        commandManager.addCommand(new SenderLimitCommand());
        commandManager.addCommand(new GameCommand());

        ikfir = server.addPlayer("ikfir");
    }

    @Test
    void startup() {
        assertNotNull(commandManager);
    }

    @Test
    void coolTest() {
        execute(ikfir, "test cool");
        assertEquals("Cool!", ikfir.nextMessage());
    }

    @Test
    void noArgs() {
        execute(ikfir, "test");
        assertEquals("No args", ikfir.nextMessage());
    }

    @Test
    void teleportTo() {
        PlayerMock voigon = server.addPlayer("Voigon");
        PlayerMock thebotgame = server.addPlayer("Thebotgame");

        voigon.teleport(new Location(voigon.getWorld(), 30, 60, 30));
        thebotgame.teleport(new Location(thebotgame.getWorld(), 100, 59, 213));

        assertEquals(voigon.getLocation(), new Location(voigon.getWorld(), 30, 60, 30));
        assertEquals(thebotgame.getLocation(), new Location(thebotgame.getWorld(), 100, 59, 213));

        execute(ikfir, "test tp voigon thebotgame");

        assertEquals("Teleporting Voigon to Thebotgame", ikfir.nextMessage());
        assertEquals(voigon.getLocation(), thebotgame.getLocation());

        execute(ikfir, "test tp voigon");
        assertEquals("Teleporting ikfir to Voigon", ikfir.nextMessage());
    }

    @Test
    void giveItem() {
        execute(ikfir, "test give ikfir DIAMOND_SWORD");
        assertEquals("Given 1 DIAMOND_SWORD to ikfir", ikfir.nextMessage());
        assertEquals(Material.DIAMOND_SWORD, ikfir.getInventory().getItem(0).getType());
        assertEquals(1, ikfir.getInventory().getItem(0).getAmount());

        ikfir.getInventory().clear();

        PlayerMock voigon = server.addPlayer("Voigon");
        execute(ikfir, "test give Voigon DIAMOND_SWORD");
        assertEquals("Given 1 DIAMOND_SWORD to Voigon", ikfir.nextMessage());

        assertEquals(Material.DIAMOND_SWORD, voigon.getInventory().getItem(0).getType());
        assertEquals(1, voigon.getInventory().getItem(0).getAmount());


        execute(ikfir, "test give ikfir DIRT 5");
        assertEquals("Given 5 DIRT to ikfir", ikfir.nextMessage());
        assertEquals(Material.DIRT, ikfir.getInventory().getItem(0).getType());
        assertEquals(5, ikfir.getInventory().getItem(0).getAmount());
    }

    @Test
    void whoIs() {
        PlayerMock voigon = server.addPlayer("Voigon");

        execute(ikfir, "test who voigon");
        assertEquals("Who is Voigon? " + voigon.getUniqueId(), ikfir.nextMessage());

        ikfir.hidePlayer(plugin, voigon);

        execute(ikfir, "test who voigon");
        assertEquals("idk who is voigon", ikfir.nextMessage());

        ikfir.showPlayer(plugin, voigon);

        execute(ikfir, "test who voigon");
        assertEquals("Who is Voigon? " + voigon.getUniqueId(), ikfir.nextMessage());
    }

    @Test
    void whoAmITest() {
        execute(ikfir, "senderlimit whoami");
        assertEquals("I'm a PLAYER", ikfir.nextMessage());

        ConsoleCommandSenderMock consoleSender = server.getConsoleSender();
        execute(consoleSender, "senderlimit whoami");
        assertEquals("I'm a CONSOLE", consoleSender.nextMessage());
    }

    @Test
    void onlyPlayerAndNoPlayerTest() {
        execute(ikfir, "senderlimit meow");
        assertEquals("I'm a player", ikfir.nextMessage());

        ConsoleCommandSenderMock consoleSender = server.getConsoleSender();
        execute(consoleSender, "senderlimit meow");
        assertEquals("I'm not a player", consoleSender.nextMessage());
    }

    @Test
    void gameCommandTest() {
        execute(ikfir, "game create");
        assertEquals("Created game", ikfir.nextMessage());

        UUID gameUniqueId = UUID.fromString(ikfir.nextMessage());

        try {
            execute(ikfir, "game create");
            fail("Should have thrown an exception");
        } catch (RuntimeException e) {
            assertEquals("net.apartium.cocoabeans.commands.spigot.game.commands.utils.NotInGameException: Player is in game", e.getMessage());
        }

        execute(ikfir, "game am i playing");
        assertEquals("Yes", ikfir.nextMessage());

        execute(ikfir, "game start");
        assertEquals("gamePlayer: " + ikfir.getUniqueId(), ikfir.nextMessage());
        assertEquals("game: " + gameUniqueId, ikfir.nextMessage());

        assertEquals("Game started in 10 seconds", ikfir.nextMessage());
    }

    @Test
    void tabCompletionMaterial() {

        assertTrue(CollectionHelpers.equalsList(
                evaluateTabCompletion(ikfir, "test", "give ikfir dirt"),
                List.of("DIRT", "DIRT_PATH")
        ));
    }

    @Test
    void testWithoutPermission() {
        execute(ikfir, "test permission");
        assertEquals("You don't have permission to execute this command", ikfir.nextMessage());
    }

    @Test
    void testWithPermission() {
        ikfir.addAttachment(plugin, "cocoabeans.test", true);
        execute(ikfir, "test permission");
        assertEquals("You have permission!", ikfir.nextMessage());
    }


}
