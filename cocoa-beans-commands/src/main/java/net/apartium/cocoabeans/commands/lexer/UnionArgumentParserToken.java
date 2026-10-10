package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A token that represents an union argument parser.
 */
@ApiStatus.AvailableSince("0.0.54")
public abstract non-sealed class UnionArgumentParserToken extends CommandToken {

    /**
     * Create a new argument parser token
     * @param from starting index
     * @param to ending index
     * @param text the entire text of command
     */
    protected UnionArgumentParserToken(int from, int to, String text) {
        super(from, to, text);
    }

    /**
     * Get the type of the token
     * @return CommandTokenType.UNION_ARGUMENT_PARSER
     */
    @Override
    public CommandTokenType getType() {
        return CommandTokenType.UNION_ARGUMENT_PARSER;
    }

    public abstract List<String> getParserNames();

    /**
     * Gets the argument parser from the given parsers map
     * @param parsers parser map that represents all argument parsers we have
     * @return the argument parser
     */
    public abstract RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers);

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
