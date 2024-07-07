package net.apartium.cocoabeans.commands.spigot;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.spigot.parsers.LocationParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandsSpigotTest extends CommandsSpigotTestBase {

    PlayerMock ikfir;

    @BeforeEach
    void setupTestCommand() {
        commandManager.registerArgumentTypeHandler(SpigotCommandManager.COMMON_PARSERS);
        commandManager.registerArgumentTypeHandler(SpigotCommandManager.SPIGOT_PARSERS);
        commandManager.registerArgumentTypeHandler(new LocationParser(0));

        commandManager.addCommand(new CommandForTest());

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
    }

    @Test
    void giveItem() {
        execute(ikfir, "test give ikfir DIAMOND_SWORD");
        assertEquals("Given 1 DIAMOND_SWORD to ikfir", ikfir.nextMessage());
        assertEquals(Material.DIAMOND_SWORD, ikfir.getInventory().getItem(0).getType());
        assertEquals(1, ikfir.getInventory().getItem(0).getAmount());

        ikfir.getInventory().clear();

        execute(ikfir, "test give ikfir DIRT 5");
        assertEquals("Given 5 DIRT to ikfir", ikfir.nextMessage());
        assertEquals(Material.DIRT, ikfir.getInventory().getItem(0).getType());
        assertEquals(5, ikfir.getInventory().getItem(0).getAmount());
    }

    @Test
    void location() {
        execute(ikfir, "test location world 30 60 30");
        assertEquals("Is that your location? world 30.0 60.0 30.0", ikfir.nextMessage());
    }

}
