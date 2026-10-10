package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.EmptyInputCapableParser;
import net.apartium.cocoabeans.commands.parsers.UnionCappableParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple argument parser token is a token that represents an argument parser
 * @see ArgumentParserToken
 */
@ApiStatus.AvailableSince("0.0.37")
public class SimpleUnionArgumentParserToken extends UnionArgumentParserToken {

    private static final Pattern PARAMETER_NAME_REGEX = Pattern.compile("^[a-zA-Z0-9_\\-]+:");

    private List<String> parsersKeywords;
    private String parameterName;
    private boolean optionalNotMatch;
    private boolean isOptional;

    /**
     * Create a new argument parser token
     * @param from starting index
     * @param to ending index
     * @param text the entire text of command
     */
    public SimpleUnionArgumentParserToken(int from, int to, String text) {
        super(from, to, text);

        // Remove <>
        String actualData = text.substring(from + 1, to - 1);

        setKeywordAndParameterName(actualData);
    }

    @Override
    public List<String> getParserNames() {
        return parsersKeywords;
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
        List<ArgumentParser<?>> argumentParsers = new ArrayList<>();
        for (String keyword : parsersKeywords) {
            ArgumentParser<?> argumentParser = parsers.get(keyword);
            if (argumentParser == null)
                throw new IllegalArgumentException("Parser not found: " + keyword);

            if (argumentParser instanceof UnionCappableParser<?>)
                argumentParsers.add(argumentParser);
            else
                throw new IllegalArgumentException("Parser " + keyword + " is not a union cappable parser");
        }

        ArgumentParser<?> parser = argumentParsers.get(0);

        if (argumentParsers.size() == 1)
            return new RegisterArgumentParser<>(parser, optionalNotMatch, isOptional, parameterName);

        try {
            UnionCappableParser<?> unionCappableParser = (UnionCappableParser<?>) parser;
            ArgumentParser<?> union = unionCappableParser.union(argumentParsers.subList(1, argumentParsers.size()).toArray(new ArgumentParser[0]));

            if (union instanceof EmptyInputCapableParser)
                isOptional = true;

            return new RegisterArgumentParser<>(union, optionalNotMatch, isOptional, parameterName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Parser " + parser.getKeyword() + " is not a union cappable parser", e);
        }
    }

    private void setKeywordAndOptionals(String keyword) {
        this.optionalNotMatch = keyword.startsWith("!") || keyword.startsWith("?!");
        this.isOptional = keyword.startsWith("?") || keyword.startsWith("!?");

        String parsersKeywords = keyword.substring((optionalNotMatch ? 1 : 0) + (isOptional ? 1 : 0));
        String[] parsers = parsersKeywords.split("\\+");
        for (String parser : parsers) {
            if (parser.isEmpty())
                throw new IllegalArgumentException("Invalid argument parser token: " + keyword);
        }

        this.parsersKeywords = List.of(parsers);
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
                ", parsersKeywords='" + parsersKeywords + '\'' +
                ", optionalNotMatch=" + optionalNotMatch +
                ", isOptional=" + isOptional +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleUnionArgumentParserToken that = (SimpleUnionArgumentParserToken) o;
        return Objects.equals(from, that.from)
                && Objects.equals(to, that.to)
                && optionalNotMatch == that.optionalNotMatch
                && isOptional == that.isOptional
                && Objects.equals(text, that.text)
                && Objects.equals(parsersKeywords, that.parsersKeywords)
                && Objects.equals(parameterName, that.parameterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text, isOptional, optionalNotMatch, parsersKeywords, parameterName);
    }
}
