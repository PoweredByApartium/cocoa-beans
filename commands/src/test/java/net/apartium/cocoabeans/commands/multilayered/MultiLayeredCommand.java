package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.TestSender;
import net.apartium.cocoabeans.commands.parsers.DummyParser;
import net.apartium.cocoabeans.commands.parsers.StringParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;

class MultiLayeredCommand extends MultiLayeredCommandBase {

    @Override
    public void greetUser(TestSender sender) {
        sender.sendMessage("Hello!");
    }

    @Override
    public void onlineUsers(TestSender sender) {
        sender.sendMessage("There are 20 users online!");
    }

    @Override
    public void showUsage(TestSender sender) {
        sender.sendMessage("Multilayered Usage");
        sender.sendMessage("/multilayered greet - greets user.");
        sender.sendMessage("/multilayered admin - online users command.");
        sender.sendMessage("/multilayered admin - admin command.");
        sender.sendMessage("/multilayered admin - meowing at a user.");
    }

    @Override
    public void admin(TestSender sender) {
        sender.sendMessage("Whoa! go fuck yourself!");
    }

    @WithParser(value = StringParser.class, priority = 9)
    @Override
    public void lol(TestSender sender, String string) {
        sender.sendMessage("Lol " + string);
    }

    @Override
    public void meow(TestSender sender) {
        sender.sendMessage("Meow!");
    }
}
