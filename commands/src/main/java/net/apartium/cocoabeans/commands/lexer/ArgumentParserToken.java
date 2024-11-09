package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.AvailableSince("0.0.37")
public class ArgumentParserToken extends CommandToken {

    // TODO may make it abstract class so people can create there own implementation

    public static final Pattern PARAMETER_NAME_REGEX = Pattern.compile("^[a-zA-Z0-9_\\-]+:");

    private String parserKeyword;
    private Optional<String> parameterName;
    private boolean optionalNotMatch;
    private boolean isOptional;

    public ArgumentParserToken(int from, int to, String text) {
        super(from, to, text);

        // Remove <>
        String actualData = text.substring(from + 1, to - 1);

        setKeywordAndParameterName(actualData);
    }

    @Override
    public CommandTokenType getType() {
        return CommandTokenType.ARGUMENT_PARSER;
    }

    public Optional<String> getParameterName() {
        return parameterName;
    }

    public RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers) {
        ArgumentParser<?> argumentParser = parsers.get(parserKeyword);
        if (argumentParser == null)
            throw new IllegalArgumentException("Parser not found: " + parserKeyword);

        return new RegisterArgumentParser<>(argumentParser, optionalNotMatch, isOptional, parameterName);
    }

    private void setKeywordAndOptionals(String keyword) {
        this.optionalNotMatch = keyword.startsWith("!") || keyword.startsWith("?!");
        this.isOptional = keyword.startsWith("?") || keyword.startsWith("!?");

        this.parserKeyword = keyword.substring((optionalNotMatch ? 1 : 0) + (isOptional ? 1 : 0));
    }

    private void setKeywordAndParameterName(String actualData) {
        String[] split = actualData.split("\\s+");
        if (split.length == 1) {
            setKeywordAndOptionals(split[0]);

            this.parameterName = Optional.empty();
            return;
        }

        if (split.length != 2)
            throw new IllegalArgumentException("Invalid argument parser token: " + actualData);

        Matcher matcher = PARAMETER_NAME_REGEX.matcher(split[0]);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid parameter name: " + actualData);

        setKeywordAndOptionals(split[1]);
        this.parameterName = Optional.of(split[0].substring(0, split[0].length() - 1));
    }

    @Override
    public String toString() {
        return "ArgumentParserToken{" +
                "parameterName=" + parameterName +
                ", from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                ", parserKeyword='" + parserKeyword + '\'' +
                ", optionalNotMatch=" + optionalNotMatch +
                ", isOptional=" + isOptional +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArgumentParserToken that = (ArgumentParserToken) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(text, that.text) && Objects.equals(parserKeyword, that.parserKeyword) && Objects.equals(parameterName, that.parameterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text, parserKeyword, parameterName);
    }
}
