package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.EvaluationContext;
import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.lexer.ArgumentParserToken;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.lexer.SimpleKeywordToken;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.CompoundParser;
import net.apartium.cocoabeans.commands.parsers.ParserVariant;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class EvilCompoundParser extends CompoundParser<Instant> {

    protected EvilCompoundParser(String keyword, int priority) {
        super(keyword, Instant.class, priority, new EvaluationContext(new SimpleCommandLexer(BasicArgumentParserToken::new, SimpleKeywordToken::new), new SimpleArgumentMapper()));
    }

    @ParserVariant("<long>")
    public Instant evilNoParserAhah(long time) {
        return Instant.ofEpochMilli(time);

    }

    private static class BasicArgumentParserToken extends ArgumentParserToken {

        protected BasicArgumentParserToken(int from, int to, String text) {
            super(from, to, text);
        }

        @Override
        public RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers) {
            return null;
        }

        @Override
        public RegisterArgumentParser<?> getParser(Map<String, ArgumentParser<?>> parsers, ArgumentParser<?> fallback) {
            return null;
        }

        @Override
        public String getParserName() {
            return "";
        }

        @Override
        public Optional<String> getParameterName() {
            return Optional.empty();
        }

        @Override
        public boolean isOptional() {
            return false;
        }

        @Override
        public boolean optionalNotMatch() {
            return false;
        }
    }

}
