package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * The Command lexer tokenize commands, and then we could use it to parse the command
 * @see SimpleCommandLexer
 */
@ApiStatus.AvailableSince("0.0.37")
public interface CommandLexer {
    /**
     * Tokenize the command into tokens
     * @param command the command to tokenize
     * @return the tokens after tokenization
     */
    List<CommandToken> tokenize(String command);

}
