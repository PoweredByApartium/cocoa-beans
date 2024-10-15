package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.parsers.exception.AmbiguousMappedKeyResponse;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidParserResponse;
import org.bukkit.GameMode;

@Command(value = "gamemode", aliases = "gm")
public class GameModeCommand implements CommandNode {

    @SubCommand("<gamemode>")
    public void selfSetGamemode(Sender sender, GameMode gamemode) {
        sender.sendMessage("Your Gamemode have been set to " + gamemode.name().toLowerCase());
    }

    @ExceptionHandle(value = InvalidParserResponse.InvalidParserException.class, priority = 1)
    public boolean invalidParser(Sender sender, InvalidParserResponse invalidParserResponse) {
        if (!(invalidParserResponse instanceof AmbiguousMappedKeyResponse ambiguousMappedKeyResponse))
            return false;

        sender.sendMessage(getDidMeanMessage(ambiguousMappedKeyResponse));
        return true;
    }

    private String getDidMeanMessage(AmbiguousMappedKeyResponse ambiguousMappedKeyResponse) {
        return "Did you mean one of the following [" + String.join(", ", ambiguousMappedKeyResponse.getKeys().stream().sorted().toList()) + "]?";
    }

    @ExceptionHandle(AmbiguousMappedKeyResponse.AmbiguousMappedKeyException.class)
    public void gamemodeCollision(Sender sender, AmbiguousMappedKeyResponse ambiguousMappedKeyResponse) {
        sender.sendMessage(getDidMeanMessage(ambiguousMappedKeyResponse));
    }

}
