package net.apartium.cocoabeans.commands.spigot.parsers;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameModeCommandTest extends CommandsSpigotTestBase {

    private PlayerMock sender;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
        commandManager.registerArgumentTypeHandler(new GameModeParser(0));

        commandManager.addCommand(new GameModeCommand());

        sender = server.addPlayer("ikfir");
    }

    @Test
    public void setSelfGameMode() {
        execute(sender, "gamemode", "c");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());
        execute(sender, "gamemode", "cr");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());
        execute(sender, "gamemode", "cre");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());
        execute(sender, "gamemode", "crea");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());
        execute(sender, "gamemode", "creat");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());
        execute(sender, "gamemode", "creati");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());
        execute(sender, "gamemode", "creativ");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());
        execute(sender, "gamemode", "creative");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());

        execute(sender, "gamemode", "CreaTive");
        assertEquals("Your Gamemode have been set to creative", sender.nextMessage());

        for (int i = 0; i < 100; i++) {
            execute(sender, "gamemode", "s");
            assertEquals("Did you mean one of the following [spectator, survival]?", sender.nextMessage());
        }

        execute(sender, "gamemode", "su");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());
        execute(sender, "gamemode", "sur");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());
        execute(sender, "gamemode", "surv");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());
        execute(sender, "gamemode", "survi");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());
        execute(sender, "gamemode", "surviv");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());
        execute(sender, "gamemode", "surviva");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());
        execute(sender, "gamemode", "survival");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());

        execute(sender, "gamemode", "sUrVivAl");
        assertEquals("Your Gamemode have been set to survival", sender.nextMessage());

        execute(sender, "gamemode", "a");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "ad");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "adv");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "adve");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "adven");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "advent");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "adventu");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "adventur");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "adventure");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());
        execute(sender, "gamemode", "aDvEnTurE");
        assertEquals("Your Gamemode have been set to adventure", sender.nextMessage());

        execute(sender, "gamemode", "sp");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
        execute(sender, "gamemode", "spe");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
        execute(sender, "gamemode", "spec");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
        execute(sender, "gamemode", "spect");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
        execute(sender, "gamemode", "specta");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
        execute(sender, "gamemode", "spectat");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
        execute(sender, "gamemode", "spectato");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
        execute(sender, "gamemode", "spectator");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());

        execute(sender, "gamemode", "sPeCtatOr");
        assertEquals("Your Gamemode have been set to spectator", sender.nextMessage());
    }

    @Test
    public void setSelfGameModeTabCompletion() {
        assertTrue(CollectionHelpers.equalsList(
                List.of("0", "1", "2", "3", "survival", "creative", "adventure", "spectator"),
                evaluateTabCompletion(sender, "gamemode", "")
        ));

        assertTrue(CollectionHelpers.equalsList(
                List.of("survival", "spectator"),
                evaluateTabCompletion(sender, "gamemode", "s")
        ));

        assertTrue(CollectionHelpers.equalsList(
                List.of("survival", "spectator"),
                evaluateTabCompletion(sender, "gamemode", "S")
        ));
    }
    

}
