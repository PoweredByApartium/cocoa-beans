package net.apartium.cocoabeans.commands.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleCommandLexerTest {

    @Test
    void justKeyword() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        List<CommandToken> tokens = lexer.tokenization("hello");

        assertEquals(List.of(new TestKeywordToken(0, 5, "hello")), tokens);

        tokens = lexer.tokenization("hello world");

        assertEquals(List.of(new TestKeywordToken(0, 5, "hello"), new TestKeywordToken(6, 11, "world")), tokens);

        tokens = lexer.tokenization("test command parser");

        assertEquals(List.of(new TestKeywordToken(0, 4, "test"), new TestKeywordToken(5, 12, "command"), new TestKeywordToken(13, 19, "parser")), tokens);
    }

    @Test
    void justArgumentParser() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        List<CommandToken> tokens = lexer.tokenization("<int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 5, "int", Optional.empty(), false, false)), tokens);

        tokens = lexer.tokenization("<amount: int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 13, "int", Optional.of("amount"), false, false)), tokens);

        tokens = lexer.tokenization("<amount: ?int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 14, "int", Optional.of("amount"), false, true)), tokens);

        tokens = lexer.tokenization("<amount: !int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 14, "int", Optional.of("amount"), true, false)), tokens);

        tokens = lexer.tokenization("<amount: !?int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 15, "int", Optional.of("amount"), true, true)), tokens);

        tokens = lexer.tokenization("<amount: ?!int>");
        assertEquals(List.of(new TestArgumentParserToken(0, 15, "int", Optional.of("amount"), true, true)), tokens);

        tokens = lexer.tokenization("<string> <amount: ?int>");
        assertEquals(List.of(
                new TestArgumentParserToken(0, 8, "string", Optional.empty(), false, false),
                new TestArgumentParserToken(9, 23, "int", Optional.of("amount"), false, true)
        ), tokens);

        tokens = lexer.tokenization("<string> <amount: int> <int>");
        assertEquals(List.of(
                new TestArgumentParserToken(0, 8, "string", Optional.empty(), false, false),
                new TestArgumentParserToken(9, 22, "int", Optional.of("amount"), false, false),
                new TestArgumentParserToken(23, 28, "int", Optional.empty(), false, false)
        ), tokens);

    }

    @Test
    void combined() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        List<CommandToken> tokens = lexer.tokenization("meow <int>");

        assertEquals(List.of(
                new TestKeywordToken(0, 4, "meow"),
                new TestArgumentParserToken(5, 10, "int", Optional.empty(), false, false)
        ), tokens);
    }

    @Test
    void errorTest() {
        SimpleCommandLexer lexer = new SimpleCommandLexer();

        assertThrows(IllegalArgumentException.class, () -> lexer.tokenization("<int"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenization("<int<>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenization("<>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenization("test<int>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenization("test>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenization("test: test wtf>"));
        assertThrows(IllegalArgumentException.class, () -> lexer.tokenization("lol$: int>"));
    }

}
