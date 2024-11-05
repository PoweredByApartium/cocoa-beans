package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.AvailableSince("0.0.37")
public class ArgumentParserToken extends CommandToken {

    public static final Pattern PARAMETER_NAME_REGEX = Pattern.compile("^[a-zA-Z0-9_\\-]+:");

    private final String parserKeyword;
    private final Optional<String> parameterName;

    public ArgumentParserToken(int from, int to, String text) {
        super(from, to, text);

        // Remove <>
        String actualData = text.substring(from + 1, to - 1);

        String[] keywordAndParameterName = getKeywordAndParameterName(actualData);
        if (keywordAndParameterName == null)
            throw new IllegalArgumentException("Invalid argument parser token: " + actualData);

        this.parameterName = Optional.ofNullable(keywordAndParameterName[1]);
        this.parserKeyword = keywordAndParameterName[0];
    }

    @Override
    public CommandTokenType getType() {
        return CommandTokenType.ARGUMENT_PARSER;
    }

    public Optional<String> getParameterName() {
        return parameterName;
    }

    public ArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers) {
        return parsers.get(parserKeyword);
    }

    private static String[] getKeywordAndParameterName(String actualData) {
        String[] split = actualData.split("\\s+");
        if (split.length == 1)
            return new String[]{split[0], null};

        if (split.length != 2)
            return null;

        Matcher matcher = PARAMETER_NAME_REGEX.matcher(split[0]);
        if (!matcher.matches())
            return null;

        return new String[]{split[1], split[0].substring(0, split[0].length() - 1)};
    }

}
