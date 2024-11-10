package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.lexer.CommandToken;

public class UnknownTokenException extends IllegalArgumentException {

    private final CommandToken token;

    public UnknownTokenException(CommandToken token) {
        super("Unknown token while parsing: " + token);

        this.token = token;
    }

    public CommandToken getToken() {
        return token;
    }
}
