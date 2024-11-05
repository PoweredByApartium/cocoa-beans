package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.37")
public abstract class CommandToken {

    protected final int from;
    protected final int to;
    protected final String text;

    protected CommandToken(int from, int to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public abstract CommandTokenType getType();

}
