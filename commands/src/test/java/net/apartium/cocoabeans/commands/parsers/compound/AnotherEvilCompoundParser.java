package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.CompoundParser;
import net.apartium.cocoabeans.commands.parsers.ParserVariant;

public class AnotherEvilCompoundParser extends CompoundParser<String> {

    public AnotherEvilCompoundParser(String keyword,  int priority) {
        super(keyword, String.class, priority, new SimpleArgumentMapper(), new SimpleCommandLexer());
    }

    @ParserVariant("ok")
    public String toOk() {
        return "ok";
    }

}
