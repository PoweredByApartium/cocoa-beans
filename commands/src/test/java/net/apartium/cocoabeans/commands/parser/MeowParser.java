package net.apartium.cocoabeans.commands.parser;

import net.apartium.cocoabeans.commands.parsers.CompoundParser;
import net.apartium.cocoabeans.commands.parsers.ParserVariant;
import net.apartium.cocoabeans.commands.parsers.SourceParser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MeowParser extends CompoundParser<Meow> {


    public MeowParser(int priority) {
        super(MeowParser.class, "meow", Meow.class, priority);
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
