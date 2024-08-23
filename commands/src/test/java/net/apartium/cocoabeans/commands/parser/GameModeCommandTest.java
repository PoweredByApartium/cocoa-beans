package net.apartium.cocoabeans.commands.parser;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameModeCommandTest extends CommandTestBase {

    @Override
    @BeforeEach
    public void before() {
        super.before();
        testCommandManager.registerArgumentTypeHandler(new GameModeParser(0));

        testCommandManager.addCommand(new GameModeCommand());
    }

    @Test
    public void setSelfGameMode() {
        evaluate("gamemode", "c");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());
        evaluate("gamemode", "cr");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());
        evaluate("gamemode", "cre");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());
        evaluate("gamemode", "crea");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());
        evaluate("gamemode", "creat");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());
        evaluate("gamemode", "creati");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());
        evaluate("gamemode", "creativ");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());
        evaluate("gamemode", "creative");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());

        evaluate("gamemode", "CreaTive");
        assertEquals(List.of("Your Gamemode have been set to creative"), sender.getMessages());

        evaluate("gamemode", "s");
        assertEquals(List.of("Did you mean one of the following [survival, spectator]?", "Did you mean one of the following [survival, spectator]?"), sender.getMessages());

        evaluate("gamemode", "su");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());
        evaluate("gamemode", "sur");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());
        evaluate("gamemode", "surv");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());
        evaluate("gamemode", "survi");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());
        evaluate("gamemode", "surviv");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());
        evaluate("gamemode", "surviva");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());
        evaluate("gamemode", "survival");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());

        evaluate("gamemode", "sUrVivAl");
        assertEquals(List.of("Your Gamemode have been set to survival"), sender.getMessages());

        evaluate("gamemode", "a");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "ad");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "adv");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "adve");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "adven");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "advent");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "adventu");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "adventur");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "adventure");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());
        evaluate("gamemode", "aDvEnTurE");
        assertEquals(List.of("Your Gamemode have been set to adventure"), sender.getMessages());

        evaluate("gamemode", "sp");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
        evaluate("gamemode", "spe");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
        evaluate("gamemode", "spec");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
        evaluate("gamemode", "spect");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
        evaluate("gamemode", "specta");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
        evaluate("gamemode", "spectat");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
        evaluate("gamemode", "spectato");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
        evaluate("gamemode", "spectator");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());

        evaluate("gamemode", "sPeCtatOr");
        assertEquals(List.of("Your Gamemode have been set to spectator"), sender.getMessages());
    }

    @Test
    public void setSelfGameModeTabCompletion() {
        assertTrue(CollectionHelpers.equalsList(
                List.of("0", "1", "2", "3", "survival", "creative", "adventure", "spectator"),
                evaluateTabCompletion("gamemode", "")
        ));

        assertTrue(CollectionHelpers.equalsList(
                List.of("survival", "spectator"),
                evaluateTabCompletion("gamemode", "s")
        ));

        assertTrue(CollectionHelpers.equalsList(
                List.of("survival", "spectator"),
                evaluateTabCompletion("gamemode", "S")
        ));
    }

    List<String> evaluateTabCompletion(String label, String args) {
        return testCommandManager.handleTabComplete(sender, label, args.split("\\s+"));
    }

    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }

}
