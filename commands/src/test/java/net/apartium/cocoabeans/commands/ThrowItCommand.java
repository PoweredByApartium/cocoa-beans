package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.SourceParser;

@Command("throw-it")
public class ThrowItCommand implements CommandNode {

    @SourceParser(keyword = "throw", clazz = String.class)
    public void to() {

    }

    @SubCommand("<throw>")
    public void throwIt(Sender sender) {
        sender.sendMessage("HOw?");
    }

}
