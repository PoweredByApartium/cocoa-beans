package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.TestSender;
import net.apartium.cocoabeans.commands.parsers.StringParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;

import java.util.Map;

@Command("another-multilayered")
public class AnotherMultiLayeredCommand extends MultiLayeredCommandBase {

    @Override
    public void greetUser(TestSender sender) {
        sender.sendMessage("Get off my server!");
    }

    @Override
    public void showUsage(TestSender sender) {
        sender.sendMessage("Another Multilayered Usage");
    }

    @Override
    public void onlineUsers(TestSender sender) {
        sender.sendMessage("Only you are online!");
        sender.sendMessage("Kinda sad that you're online");
    }

    @Override
    public void admin(TestSender sender) {
        sender.sendMessage("You're not worthy to be an admin!!!");
        sender.sendMessage("You feel so bad for being an admin");
        sender.sendMessage("You are so bad for being an admin");
        sender.sendMessage("Get a job, You discord mod!");
        sender.sendMessage("\"Laugh at my bad jokes!\" - A Discord moderator");
    }

    @Override
    public void lol(TestSender sender, int times) {
        sender.sendMessage("Witch craft!!!");
    }

    @WithParser(value = StringParser.class, priority = 11)
    @Override
    public void lol(TestSender sender, String string) {
        sender.sendMessage("Lol " + string);
    }

    @Override
    public void meow(TestSender sender) {
        sender.sendMessage("Very cool interface");
    }

    @Override
    public Map<String, String> meow() {
        return Map.of(
                "meow", "I'm a cat!",
                "woof", "I'm a dog!"
        );
    }

    @SubCommand("mystery <meow>")
    public void mystery(TestSender sender, String meow) {
        sender.sendMessage("Mystery " + meow);
    }
}
