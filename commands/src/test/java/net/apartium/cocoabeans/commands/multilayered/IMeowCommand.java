package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.TestSender;

public interface IMeowCommand {

    @SubCommand("meow")
    void meow(TestSender sender);

}
