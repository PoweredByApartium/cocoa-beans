package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.parsers.SourceParser;

import java.util.Map;

public interface MySourceParser {

    @SourceParser(keyword = "meow", clazz = String.class, resultMaxAgeInMills = -1)
    Map<String, String> meow();

}
