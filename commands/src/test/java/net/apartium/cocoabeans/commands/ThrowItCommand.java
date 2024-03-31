package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.SourceParser;

import java.util.Map;

@Command("throw-it")
public class ThrowItCommand implements CommandNode {

    @SourceParser(keyword = "throw", clazz = String.class)
    public void to() {

    }

}
