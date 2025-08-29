package net.apartium.cocoabeans.commands.spigot.parsers;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeowCommandTest extends CommandsSpigotTestBase {

    private PlayerMock player;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        commandManager.registerArgumentTypeHandler(new MeowParser(0));
        commandManager.addCommand(new CatCommand());

        player = server.addPlayer("ikfir");
    }

    @Test
    void setCommand() {
        execute(player, "cat", "set tom a_cat 13 male");
        assertEquals("tom has been set to cat: a_cat, age: 13, gender: MALE", player.nextMessage());
    }


}
