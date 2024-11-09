package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * A token that represents an argument parser.
 */
@ApiStatus.AvailableSince("0.0.37")
public abstract class ArgumentParserToken extends CommandToken {

    public static final Pattern PARAMETER_NAME_REGEX = Pattern.compile("^[a-zA-Z0-9_\\-]+:");

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
     * Gets the parameter name of the argument parser if it has one
     * @return the parameter name
     */
    public abstract Optional<String> getParameterName();

}
