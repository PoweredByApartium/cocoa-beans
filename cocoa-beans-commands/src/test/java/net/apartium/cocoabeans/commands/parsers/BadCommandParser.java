package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;

@Command("bad")
public class BadCommandParser implements CommandNode {

    @SubCommand("<bad-parser>")
    public void badParse(String wow) throws IllegalAccessException {
        throw new IllegalAccessException("Shouldn't happen");
    }

}
