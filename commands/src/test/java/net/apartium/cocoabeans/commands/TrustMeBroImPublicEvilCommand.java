package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.exception.ExceptionHandle;

@Command("trust-me-bro")
public class TrustMeBroImPublicEvilCommand implements CommandNode {

    @ExceptionHandle(Exception.class)
    private boolean meow(Exception exception, Sender sender) {
        throw new RuntimeException(exception);
    }

}
