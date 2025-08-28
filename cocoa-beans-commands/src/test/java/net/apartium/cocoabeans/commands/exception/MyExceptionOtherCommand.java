package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.SubCommand;

@Command("why")
public class MyExceptionOtherCommand extends MyExceptionBaseCommand {

    @SubCommand("no")
    public void no() {
        throw new MyException();
    }

}
