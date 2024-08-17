package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.IntParser;
import net.apartium.cocoabeans.commands.parsers.StringParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;

@WithParser(value = StringParser.class, priority = -1)
@Command("with-parser-edge")
public class WithParserEdgeCommand implements CommandNode {

    @WithParser(value = IntParser.class, priority = 1)
    @SubCommand("test <string>")
    public void test(Sender sender, String string) {
        sender.sendMessage("test(Sender sender, String string) " + string);
    }

    @SubCommand("test <int>")
    public void test2(Sender sender, int num) {
        sender.sendMessage("test2(Sender sender, int num) " + num);
    }

}
