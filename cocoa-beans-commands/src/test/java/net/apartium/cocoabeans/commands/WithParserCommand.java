package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.DoubleParser;
import net.apartium.cocoabeans.commands.parsers.StringParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;

@WithParser(value = DoubleParser.class, priority = 10)
@Command("withparser")
public class WithParserCommand implements CommandNode {

    @WithParser(value = StringParser.class, priority = 9)
    @SubCommand("test <string>")
    public void test(Sender sender, String string) {
        sender.sendMessage("test(Sender sender, String string) " + string);
    }

    @SubCommand("test <double>")
    public void test(Sender sender, double num) {
        sender.sendMessage("test(Sender sender, double num) " + num);
    }

}
