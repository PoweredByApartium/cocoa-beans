package net.apartium.cocoabeans.commands.virtual.multiple;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;

@Command(value = "abc", aliases = {"cba", "bca"})
public class ABCCommandSecondForTest implements CommandNode {

    @SubCommand("test b")
    public void testB(Sender sender) {

    }

}
