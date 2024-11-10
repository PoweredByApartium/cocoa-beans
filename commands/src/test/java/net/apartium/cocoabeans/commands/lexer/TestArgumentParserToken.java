package net.apartium.cocoabeans.commands.lexer;

import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TestArgumentParserToken extends ArgumentParserToken {

    private final String parserKeyword;
    private final Optional<String> parameterName;
    private final boolean optionalNotMatch;
    private final boolean isOptional;

    /**
     * Create a new argument parser token
     * @param from starting index
     * @param to   ending index
     * @param parserKeyword just the argument parser keyword
     * @param parameterName the parameter name
     */
    protected TestArgumentParserToken(int from, int to, String parserKeyword, Optional<String> parameterName, boolean optionalNotMatch, boolean isOptional) {
        super(from, to, "");

        this.parserKeyword = parserKeyword;
        this.parameterName = parameterName;

        this.optionalNotMatch = optionalNotMatch;
        this.isOptional = isOptional;
    }

    @Override
    public RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers) {
        ArgumentParser<?> argumentParser = parsers.get(parserKeyword);
        if (argumentParser == null)
            throw new IllegalArgumentException("Parser not found: " + parserKeyword);

        return new RegisterArgumentParser<>(argumentParser, optionalNotMatch, isOptional, parameterName);
    }

    @Override
    public String getParserName() {
        return parserKeyword;
    }

    @Override
    public Optional<String> getParameterName() {
        return parameterName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArgumentParserToken that)) return false;
        return from == that.from && to == that.to && optionalNotMatch == that.optionalNotMatch() && isOptional == that.isOptional() && Objects.equals(parserKeyword, that.getParserName()) && Objects.equals(parameterName, that.getParameterName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(parserKeyword, parameterName, optionalNotMatch, isOptional);
    }

    @Override
    public String toString() {
        return "TestArgumentParserToken{" +
                "parameterName=" + parameterName +
                ", from=" + from +
                ", to=" + to +
                ", parserKeyword='" + parserKeyword + '\'' +
                ", optionalNotMatch=" + optionalNotMatch +
                ", isOptional=" + isOptional +
                '}';
    }
}
