package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.EvaluationContext;
import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.*;

import java.time.Duration;
import java.util.OptionalInt;

@WithParser(StringParser.class)
@WithParser(DurationParser.class)
@WithParser(IntParser.class)
public class RecipeParser extends CompoundParser<RecipeParser.Recipe> {

    public static final String DEFAULT_KEYWORD = "recipe";

    public RecipeParser() {
        this(DEFAULT_KEYWORD, 0);
    }

    public RecipeParser(String keyword, int priority) {
        super(keyword, Recipe.class, priority, new EvaluationContext(new SimpleCommandLexer(), new SimpleArgumentMapper()));
    }

    @ParserVariant("<string> <duration>")
    public Recipe cooking(String type, Duration duration) {
        return new Recipe(type, duration, OptionalInt.empty());
    }

    @ParserVariant("<string> <duration> <int>")
    public Recipe cooking(String type, Duration duration, int difficulty) {
        return new Recipe(type, duration, OptionalInt.of(difficulty));
    }

    public record Recipe(String type, Duration duration, OptionalInt difficulty) {

    }
}
