package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;

/**
 * A token that represents an argument parser.
 */
@ApiStatus.AvailableSince("0.0.37")
public abstract class ArgumentParserToken extends CommandToken {

    /**
     * Create a new argument parser token
     * @param from starting index
     * @param to ending index
     * @param text the entire text of command
     */
    protected ArgumentParserToken(int from, int to, String text) {
        super(from, to, text);
    }

    /**
     * Get the type of the token
     * @return CommandTokenType.ARGUMENT_PARSER
     */
    @Override
    public CommandTokenType getType() {
        return CommandTokenType.ARGUMENT_PARSER;
    }

    /**
     * Gets the argument parser from the given parsers map
     * @param parsers parser map that represents all argument parsers we have
     * @return the argument parser
     */
    public abstract RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers);

    /**
     * Gets the argument parser from the given parsers map
     * @param parsers parser map that represents all argument parsers we have
     * @param fallback fallback parser when couldn't find parser in map
     * @return the argument parser or fallback
     */
    @ApiStatus.AvailableSince("0.0.41")
    public abstract RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers, ArgumentParser<?> fallback);

    /**
     * Gets the argument parser name
     * @return the argument parser name
     */
    public abstract String getParserName();

    /**
     * Gets the parameter name of the argument parser if it has one
     * @return the parameter name
     */
    public abstract Optional<String> getParameterName();

    /**
     * Gets if the argument parser is optional
     * @return true if the argument parser is optional
     */
    public abstract boolean isOptional();

    /**
     * Gets if the argument parser is optional but does not match
     * @return true if the argument parser is optional but does not match
     */
    public abstract boolean optionalNotMatch();

}
