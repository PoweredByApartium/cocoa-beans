package net.apartium.cocoabeans.commands.lexer;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleCommandLexerTest {

    @Test
    void justKeyword() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        List<CommandToken> tokens = lexer.tokenize("hello");

        assertEquals(List.of(new TestKeywordToken(0, 5, "hello")), tokens);
        assertTrue(tokens.get(0).equals(new TestKeywordToken(0, 5, "hello")));
        assertEquals(new SimpleKeywordToken(0, 5, "hello"), tokens.get(0));
        assertEquals(Objects.hash(0, 5, "hello", "hello"), tokens.get(0).hashCode());
        assertEquals("SimpleKeywordToken{keyword='hello', from=0, to=5, text='hello'}", tokens.get(0).toString());

        tokens = lexer.tokenize("hello world");

        assertEquals(List.of(new TestKeywordToken(0, 5, "hello"), new TestKeywordToken(6, 11, "world")), tokens);

        tokens = lexer.tokenize("test command parser");

        assertEquals(List.of(new TestKeywordToken(0, 4, "test"), new TestKeywordToken(5, 12, "command"), new TestKeywordToken(13, 19, "parser")), tokens);

        assertEquals(CommandTokenType.KEYWORD, tokens.get(0).getType());
        assertEquals(0, tokens.get(0).from());
        assertEquals(4, tokens.get(0).to());
    }

    @Test
    void justArgumentParser() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        List<CommandToken> tokens = lexer.tokenize("<int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 5, "int", Optional.empty(), false, false)), tokens);

        assertEquals(
                "SimpleArgumentParserToken{parameterName=Optional.empty, from=0, to=5, text='<int>', parserKeyword='int', optionalNotMatch=false, isOptional=false}",
                tokens.get(0).toString()
        );
        assertEquals(
                new SimpleArgumentParserToken(0, 5, "<int>"),
                tokens.get(0)
        );

        tokens = lexer.tokenize("<amount: int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 13, "int", Optional.of("amount"), false, false)), tokens);

        tokens = lexer.tokenize("<amount: ?int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 14, "int", Optional.of("amount"), false, true)), tokens);

        tokens = lexer.tokenize("<amount: !int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 14, "int", Optional.of("amount"), true, false)), tokens);

        tokens = lexer.tokenize("<amount: !?int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 15, "int", Optional.of("amount"), true, true)), tokens);

        tokens = lexer.tokenize("<amount: ?!int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 15, "int", Optional.of("amount"), true, true)), tokens);

        tokens = lexer.tokenize("<string> <amount: ?int>");
        assertEquals(List.of(
                new TestArgumentParserToken(0, 8, "string", Optional.empty(), false, false),
                new TestArgumentParserToken(9, 23, "int", Optional.of("amount"), false, true)
        ), tokens);

        tokens = lexer.tokenize("<string> <amount: int> <int>");
        assertEquals(List.of(
                new TestArgumentParserToken(0, 8, "string", Optional.empty(), false, false),
                new TestArgumentParserToken(9, 22, "int", Optional.of("amount"), false, false),
                new TestArgumentParserToken(23, 28, "int", Optional.empty(), false, false)
        ), tokens);

        assertEquals(CommandTokenType.ARGUMENT_PARSER, tokens.get(0).getType());
        assertEquals(
                Objects.hash(
                        0,
                        8,
                        "<string> <amount: int> <int>",
                        false,
                        false,
                        "string",
                        Optional.empty()
                ),
                tokens.get(0).hashCode()
        );

    }

    @Test
    void combined() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        List<CommandToken> tokens = lexer.tokenize("meow <int>");

        assertEquals(List.of(
                new TestKeywordToken(0, 4, "meow"),
                new TestArgumentParserToken(5, 10, "int", Optional.empty(), false, false)
        ), tokens);
    }

    @Test
    void errorTest() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("<int"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("<int<>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("<>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("test<int>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("test>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("test: test wtf>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("<test: test wtf>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenize("<lol$: int>"));
    }

}
