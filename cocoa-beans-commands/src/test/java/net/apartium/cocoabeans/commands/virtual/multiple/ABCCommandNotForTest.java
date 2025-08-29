package net.apartium.cocoabeans.commands.virtual.multiple;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;

@Command(value = "abc", aliases = {"cba"})
public class ABCCommandNotForTest implements CommandNode {

    @SubCommand("test c")
    public void testC(Sender sender) {

    }

}
