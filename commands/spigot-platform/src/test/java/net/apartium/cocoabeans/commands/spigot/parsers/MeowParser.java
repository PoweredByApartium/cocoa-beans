package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@WithParser(StringParser.class)
@WithParser(IntParser.class)
public class MeowParser extends CompoundParser<Meow> {


    public MeowParser(int priority) {
        super("meow", Meow.class, priority, new SimpleArgumentMapper(), new SimpleCommandLexer());
    }

    @ParserVariant("<string> <int> <gender>")
    public Meow serialize(String cat, int age, Meow.Gender gender) {
        return new Meow(cat, age, gender);
    }


    @SourceParser(
            keyword = "gender",
            clazz = Meow.Gender.class,
            resultMaxAgeInMills = -1
    )
    public Map<String, Meow.Gender> getGenders() {
        return Arrays.stream(Meow.Gender.values())
                .collect(Collectors.toMap(
                        value -> value.name().toLowerCase(),
                        value -> value
                ));
    }

}
