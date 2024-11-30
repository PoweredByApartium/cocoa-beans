package net.apartium.cocoabeans.commands.spigot.requirements;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PermissionCommandTest extends CommandsSpigotTestBase {

    PlayerMock player;

    @BeforeEach
    @Override
    public void setup() {
        super.setup();

        commandManager.addCommand(new PermissionCommand());

        player = server.addPlayer("ikfir");
    }

    @Test
    void testAddPermission() {
        assertFalse(player.hasPermission("my.test"));
        addPermission(player, "my.test");
        assertTrue(player.hasPermission("my.test"));
    }

    @Test
    void testWithPermission() {
        addPermission(player, "my.test");
        execute(player, "permission", "test");
        assertEquals("You got a permission", player.nextMessage());
    }

    @Test
    void testWithoutPermission() {
        execute(player, "permission", "test");
        assertEquals("You don't have a permission", player.nextMessage());
    }

    @Test
    void testWithAndWithoutPermission() {
        execute(player, "permission", "test");
        assertEquals("You don't have a permission", player.nextMessage());

        addPermission(player, "my.test");
        execute(player, "permission", "test");
        assertEquals("You got a permission", player.nextMessage());
    }

    @Test
    void wowTestWithAndWithoutPermission() {
        execute(player, "permission", "wow");
        assertEquals("nah", player.nextMessage());

        addPermission(player, "my.test");
        execute(player, "permission", "wow");
        assertEquals("You should not have permission to execute this command", player.nextMessage());
    }

    void addPermission(Player target, String permission) {
        PermissionAttachment attachment = target.addAttachment(plugin);
        attachment.setPermission(permission, true);
    }

}
