package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;

@Command("custom")
public class CustomParserCommand implements CommandNode {

    @SubCommand("use <spell>")
    @SubCommand("kill <wizard>")
    @SubCommand("create <potion>")
    public void useKillCreate() {}

}
