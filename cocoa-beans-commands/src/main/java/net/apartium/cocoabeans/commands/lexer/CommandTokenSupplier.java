package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

/**
 * Command token supplier
 * @param <T> the type of the command token
 * @see KeywordToken
 * @see ArgumentParserToken
 */
@FunctionalInterface
@ApiStatus.AvailableSince("0.0.37")
public interface CommandTokenSupplier<T extends CommandToken> {

    /**
     * Create a new command token
     * @param from starting index
     * @param to ending index
     * @param text the entire command
     * @return the new command token
     */
    T apply(int from, int to, String text);

}
