package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;

public class MyExceptionBaseCommand implements CommandNode {

    public static class MyException extends RuntimeException {

    }

    @ExceptionHandle(MyException.class)
    public void handleMyException(Sender sender) {
        sender.sendMessage("Caught my exception");
    }

}
