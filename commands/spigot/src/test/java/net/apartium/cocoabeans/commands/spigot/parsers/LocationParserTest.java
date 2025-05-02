package net.apartium.cocoabeans.commands.spigot.parsers;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationParserTest extends CommandsSpigotTestBase {

    private PlayerMock justNOTRO;

    @BeforeEach
    @Override
    public void setup() {
        super.setup();

        commandManager.registerArgumentTypeHandler(new LocationParser(0));
        commandManager.addCommand(new TeleportCommandForTest());

        justNOTRO = server.addPlayer("JustNOTRO");
    }

    @Test
    @DisplayName("Parse x y z")
    void test3Args() {
        execute(justNOTRO, "tp 20 30 40");

        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40));
        assertEquals("Teleported to location.", justNOTRO.nextMessage());
    }

    @Test
    @DisplayName("Parse x y z with additional arg")
    void test3ArgsWithAdditional() {
        execute(justNOTRO, "tp 20 30 40 shamen");
        justNOTRO.assertSaid("shamen");
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40));
    }

    @Test
    @DisplayName("Parse x y z with additional pre arg")
    void test3ArgsWithAdditionalPre() {
        execute(justNOTRO, "tp pre shamen 20 30 40");
        justNOTRO.assertSaid("shamen");
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40));
    }


    @Test
    @DisplayName("Parse world x y z")
    void test4Args() {
        server.addSimpleWorld("world0");
        execute(justNOTRO, "tp world0 20 30 40");

        assertEquals(justNOTRO.getWorld(), Bukkit.getWorld("world0"));
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40));
    }

    @Test
    @DisplayName("Parse world x y z with additional arg")
    void test4ArgsWithAdditional() {
        server.addSimpleWorld("world0");
        execute(justNOTRO, "tp world0 20 30 40 noder");
        justNOTRO.assertSaid("noder");

        assertEquals(justNOTRO.getWorld(), Bukkit.getWorld("world0"));
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40));
    }

    @Test
    @DisplayName("Parse world x y z with additional pre arg")
    void test4ArgsWithAdditionalPre() {
        server.addSimpleWorld("world0");
        execute(justNOTRO, "tp pre noder world0 20 30 40");
        justNOTRO.assertSaid("noder");

        assertEquals(justNOTRO.getWorld(), Bukkit.getWorld("world0"));
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40));
    }

    @Test
    @DisplayName("Parse x y z y p")
    void test5Args() {
        execute(justNOTRO, "tp 20 30 40 10 20");
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40, 10, 20));
    }

    @Test
    @DisplayName("Parse x y z y p with additional arg")
    void test5ArgsWithAdditional() {
        execute(justNOTRO, "tp 20 30 40 10 20 kurdi");
        justNOTRO.assertSaid("kurdi");
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40, 10, 20));
    }

    @Test
    @DisplayName("Parse x y z y p with additional pre arg")
    void test5ArgsWithAdditionalPre() {
        execute(justNOTRO, "tp pre kurdi 20 30 40 10 20");
        justNOTRO.assertSaid("kurdi");
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40, 10, 20));
    }

    @Test
    @DisplayName("Parse world x y z y p")
    void test6Args() {
        server.addSimpleWorld("world0");
        execute(justNOTRO, "tp world0 20 30 40 10 20");

        assertEquals(justNOTRO.getWorld(), Bukkit.getWorld("world0"));
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40, 10, 20));
    }

    @Test
    @DisplayName("Parse world x y z y p with additional arg")
    void test6ArgsWithAdditionalArg() {
        server.addSimpleWorld("world0");
        execute(justNOTRO, "tp world0 20 30 40 10 20 shamennn");
        justNOTRO.assertSaid("shamennn");

        assertEquals(justNOTRO.getWorld(), Bukkit.getWorld("world0"));
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40, 10, 20));
    }

    @Test
    @DisplayName("Parse world x y z y p with additional pre arg")
    void test6ArgsWithAdditionalArgPre() {
        server.addSimpleWorld("world0");
        execute(justNOTRO, "tp pre shamennn world0 20 30 40 10 20");
        justNOTRO.assertSaid("shamennn");

        assertEquals(justNOTRO.getWorld(), Bukkit.getWorld("world0"));
        assertEquals(justNOTRO.getLocation(), new Location(justNOTRO.getWorld(), 20, 30, 40, 10, 20));
    }

    @Command("tp")
    public class TeleportCommandForTest implements CommandNode {

        @SubCommand("<location>")
        @SenderLimit(SenderType.PLAYER)
        public void teleportToLocation(Player sender, Location location) {
            sender.teleport(location);
            sender.sendMessage("Teleported to location.");
        }

        @SubCommand("<location> <string>")
        @SenderLimit(SenderType.PLAYER)
        public void teleportWithArg(Player sender, Location location, String string) {
            sender.teleport(location);
            sender.sendMessage(string);
        }

        @SubCommand("pre <string> <location>")
        @SenderLimit(SenderType.PLAYER)
        public void teleportWithPreArg(String string, Player sender, Location location) {
            sender.teleport(location);
            sender.sendMessage(string);
        }


    }

}
