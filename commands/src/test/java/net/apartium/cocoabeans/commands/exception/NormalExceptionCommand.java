package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;

@Command("test")
public class NormalExceptionCommand implements CommandNode {


    @SubCommand("throw")
    public void throwError(Sender sender) {
        sender.sendMessage("GO way");
        throw new MyException();
    }

    @ExceptionHandle(MyException.class)
    public void handleMyException(Sender sender) {
        sender.sendMessage("Caught my exception");
    }

    public static class MyException extends RuntimeException {

    }

}
