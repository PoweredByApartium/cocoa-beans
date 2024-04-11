package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.SourceParser;

@Command("throw-it")
public class CommandWithInvalidSourceParser implements CommandNode {

    @SourceParser(keyword = "throw", clazz = String.class)
    public void to() {

    }

}
