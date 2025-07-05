package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple argument parser token is a token that represents an argument parser
 * @see ArgumentParserToken
 */
@ApiStatus.AvailableSince("0.0.37")
public class SimpleArgumentParserToken extends ArgumentParserToken {

    private static final Pattern PARAMETER_NAME_REGEX = Pattern.compile("^[a-zA-Z0-9_\\-]+:");

    private static final Logger logger = LoggerFactory.getLogger(SimpleArgumentParserToken.class);

    private String parserKeyword;
    private String parameterName;
    private boolean optionalNotMatch;
    private boolean isOptional;

    /**
     * Create a new argument parser token
     * @param from starting index
     * @param to ending index
     * @param text the entire text of command
     */
    public SimpleArgumentParserToken(int from, int to, String text) {
        super(from, to, text);

        // Remove <>
        String actualData = text.substring(from + 1, to - 1);

        setKeywordAndParameterName(actualData);
    }

    /**
     * Gets the parameter name of the argument parser if it has one
     * @return the parameter name
     */
    @Override
    public Optional<String> getParameterName() {
        return Optional.ofNullable(parameterName);
    }

    /**
     * Gets if the argument parser is optional
     *
     * @return true if the argument parser is optional
     */
    @Override
    public boolean isOptional() {
        return isOptional;
    }

    /**
     * Gets if the argument parser is optional but does not match
     *
     * @return true if the argument parser is optional but does not match
     */
    @Override
    public boolean optionalNotMatch() {
        return optionalNotMatch;
    }

    /**
     * Gets the argument parser from the given parsers map
     * @param parsers parser map that represents all argument parsers we have
     * @return the argument parser
     */
    @Override
    public RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers) {
        return getParser(parsers, null);
    }

    /**
     * Gets the argument parser from the given parsers map
     * @param parsers parser map that represents all argument parsers we have
     * @param fallback fallback parser when couldn't find parser in map
     * @return the argument parser or fallback
     */
    @ApiStatus.AvailableSince("0.0.41")
    @Override
    public RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers, ArgumentParser<?> fallback) {
        ArgumentParser<?> argumentParser = parsers.get(parserKeyword);
        if (argumentParser == null) {
            if (fallback == null)
                throw new IllegalArgumentException("Parser not found: " + parserKeyword);

            logger.warn("Parser not found for: {} & fallback to: {}", parserKeyword, fallback.getClass().getSimpleName());
            return new RegisterArgumentParser<>(fallback, optionalNotMatch, isOptional, parameterName);
        }

        return new RegisterArgumentParser<>(argumentParser, optionalNotMatch, isOptional, parameterName);
    }

    /**
     * Gets the argument parser name
     * @return the argument parser name
     */
    @Override
    public String getParserName() {
        return parserKeyword;
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

            this.parameterName = null;
            return;
        }

        if (split.length != 2)
            throw new IllegalArgumentException("Invalid argument parser token: " + actualData);

        Matcher matcher = PARAMETER_NAME_REGEX.matcher(split[0]);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid parameter name: " + actualData);

        setKeywordAndOptionals(split[1]);
        this.parameterName = split[0].substring(0, split[0].length() - 1);
    }

    @Override
    public String toString() {
        return "SimpleArgumentParserToken{" +
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
        SimpleArgumentParserToken that = (SimpleArgumentParserToken) o;
        return Objects.equals(from, that.from)
                && Objects.equals(to, that.to)
                && optionalNotMatch == that.optionalNotMatch
                && isOptional == that.isOptional
                && Objects.equals(text, that.text)
                && Objects.equals(parserKeyword, that.parserKeyword)
                && Objects.equals(parameterName, that.parameterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text, isOptional, optionalNotMatch, parserKeyword, parameterName);
    }
}
