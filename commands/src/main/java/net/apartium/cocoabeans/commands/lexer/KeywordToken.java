package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

/**
 * A Keyword token that represents an keyword
 */
@ApiStatus.AvailableSince("0.0.37")
public abstract class KeywordToken extends CommandToken {

    /**
     * Create a new keyword token
     * @param from starting index
     * @param to ending index
     * @param text the entire text of command
     */
    protected KeywordToken(int from, int to, String text) {
        super(from, to, text);
    }

    /**
     * Get the type of the token
     * @return CommandTokenType.KEYWORD
     */
    @Override
    public CommandTokenType getType() {
        return CommandTokenType.KEYWORD;
    }

    /**
     * Get the keyword
     * @return the keyword
     */
    public abstract String getKeyword();

}
