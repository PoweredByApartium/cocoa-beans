package net.apartium.cocoabeans.commands.parser;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;

@Command(value = "gamemode", aliases = "gm")
public class GameModeCommand implements CommandNode {

    @SubCommand("<gamemode>")
    public void selfSetGamemode(Sender sender, GameMode gamemode) {
        sender.sendMessage("Your Gamemode have been set to " + gamemode.name().toLowerCase());
    }

}
