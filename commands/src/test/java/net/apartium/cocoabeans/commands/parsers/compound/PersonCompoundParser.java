package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.*;
import net.apartium.cocoabeans.commands.requirements.argument.Range;

@WithParser(IntParser.class)
@WithParser(StringParser.class)
public class PersonCompoundParser extends CompoundParser<PersonCompoundParser.Person> {

    public PersonCompoundParser(String keyword, int priority) {
        super(keyword, Person.class, priority, new SimpleArgumentMapper(), new SimpleCommandLexer());
    }

    public PersonCompoundParser(int priority) {
        this("person", priority);
    }

    @ParserVariant("<string> <int>")
    public Person toPerson(String name, @Range(to=120) int age) {
        return new Person(name, age);
    }

    public record Person(
            String name,
            int age
    ) {

    }

}
