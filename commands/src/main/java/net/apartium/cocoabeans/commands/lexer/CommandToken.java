package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

/**
 * Command token for parsing commands
 * @see CommandLexer
 */
@ApiStatus.AvailableSince("0.0.37")
public abstract class CommandToken {

    protected final int from;
    protected final int to;
    protected final String text;

    /**
     * Create a new command token
     * @param from starting index
     * @param to ending index
     * @param text the entire text of command
     */
    protected CommandToken(int from, int to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    /**
     * Gets the starting index
     * @return the starting index
     */
    public int from() {
        return from;
    }

    /**
     * Gets the ending index
     * @return the ending index
     */
    public int to() {
        return to;
    }

    /**
     * Get the type of the token
     * @return the type
     */
    public abstract CommandTokenType getType();

}
