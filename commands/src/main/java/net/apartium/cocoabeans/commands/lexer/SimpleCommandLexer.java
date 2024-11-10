package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple command lexer tokenize commands, and then we could use it to parse the command
 * @see CommandLexer
 */
@ApiStatus.AvailableSince("0.0.37")
public class SimpleCommandLexer implements CommandLexer {

    private final CommandTokenSupplier<ArgumentParserToken> argumentSupplier;
    private final CommandTokenSupplier<KeywordToken> keywordSupplier;

    /**
     * Create a new simple command lexer with SimpleArgumentParserToken and SimpleKeywordToken
     */
    public SimpleCommandLexer() {
        this(SimpleArgumentParserToken::new, SimpleKeywordToken::new);
    }

    /**
     * Create a new simple command lexer
     * @param argumentSupplier the argument supplier to create argument parser tokens
     * @param keywordSupplier the keyword supplier to create keyword tokens
     */
    public SimpleCommandLexer(CommandTokenSupplier<ArgumentParserToken> argumentSupplier, CommandTokenSupplier<KeywordToken> keywordSupplier) {
        this.argumentSupplier = argumentSupplier;
        this.keywordSupplier = keywordSupplier;
    }

    /**
     * Tokenize the command into tokens
     * @param command the command to tokenize
     * @return the tokens after tokenization
     */
    @Override
    public List<CommandToken> tokenization(String command) {
        List<CommandToken> tokens = new ArrayList<>();

        int from = 0;
        boolean hasData = false;
        boolean argumentParser = false;

        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            if (c == ' ') {
                if (argumentParser)
                    continue;

                handleKeyword(tokens, from, i, command);

                from = i + 1;
                hasData = false;
                continue;
            }

            if (c == '<') {
                ensureCouldOpenArgument(argumentParser, hasData);
                argumentParser = true;
                continue;
            }

            if (c == '>') {
                handleClosingArgument(tokens, from, i, command, argumentParser);

                argumentParser = false;
                from = i + 1;
                hasData = false;
                continue;
            }

            hasData = true;
        }

        if (hasData)
            handleFinalArgument(tokens, from, command, argumentParser);

        return tokens;
    }

    private void ensureCouldOpenArgument(boolean argumentParser, boolean hasData) {
        if (argumentParser)
            throw new IllegalArgumentException("Nested argument parsers are not allowed");

        if (hasData)
            throw new IllegalArgumentException("Missing keyword in argument parser");
    }

    private void handleKeyword(List<CommandToken> tokens, int from, int to, String command) {
        if (from == to)
            return;

        tokens.add(keywordSupplier.apply(from, to, command));
    }

    private void handleClosingArgument(List<CommandToken> tokens, int from, int to, String command, boolean argumentParser) {
        if (!argumentParser)
            throw new IllegalArgumentException("Missing argument parser");

        if (from == to - 1)
            throw new IllegalArgumentException("Empty argument parser");

        tokens.add(argumentSupplier.apply(from, to + 1, command));
    }

    private void handleFinalArgument(List<CommandToken> tokens, int from, String command, boolean argumentParser) {
        if (argumentParser)
            throw new IllegalArgumentException("Missing argument parser");

        if (from != command.length() - 1)
            tokens.add(keywordSupplier.apply(from, command.length(), command));
    }

}
