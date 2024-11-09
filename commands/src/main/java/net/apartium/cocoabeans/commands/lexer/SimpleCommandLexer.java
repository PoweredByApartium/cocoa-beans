package net.apartium.cocoabeans.commands.lexer;

import java.util.ArrayList;
import java.util.List;

public class SimpleCommandLexer implements CommandLexer {

    private static CommandLexer instance;

    public static CommandLexer getInstance() {
        return (instance == null ? instance = new SimpleCommandLexer() : instance);
    }

    private SimpleCommandLexer() { }

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

                if (from == i) {
                    from = i + 1;
                    hasData = false;
                    continue;
                }

                tokens.add(new KeywordToken(from, i, command));

                from = i + 1;
                hasData = false;
                continue;
            }

            if (c == '<') {
                if (argumentParser)
                    throw new IllegalArgumentException("Nested argument parsers are not allowed");

                if (hasData)
                    throw new IllegalArgumentException("Missing keyword in argument parser");

                argumentParser = true;
                continue;
            }

            if (c == '>') {
                if (!argumentParser)
                    throw new IllegalArgumentException("Missing argument parser");

                if (from == i - 1)
                    throw new IllegalArgumentException("Empty argument parser");

                argumentParser = false;
                tokens.add(new ArgumentParserToken(from, i + 1, command));

                from = i + 1;
                hasData = false;
                continue;
            }

            hasData = true;
        }

        if (hasData) {
            if (argumentParser)
                throw new IllegalArgumentException("Missing argument parser");
            else if (from != command.length() - 1)
                tokens.add(new KeywordToken(from, command.length(), command));
        }

        return tokens;
    }

}
