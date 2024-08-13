package net.apartium.cocoabeans.commands.multilayered;


import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.TestSender;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.parsers.IntParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;

import java.util.Map;

@Permission("cocoa-beans.commands.multilayered")
public abstract class MultiLayeredCommandBase implements CommandNode, IMeowCommand, MySourceParser {

    @SubCommand("greet")
    public abstract void greetUser(TestSender sender);

    @SubCommand
    public abstract void showUsage(TestSender sender);

    @SubCommand("users")
    public abstract void onlineUsers(TestSender sender); // prints the size of the users online

    @Permission("cocoa-beans.commands.multilayered.admin")
    @SubCommand("admin")
    public abstract void admin(TestSender sender);

    @SubCommand("shh")
    public void shh(TestSender sender) {
        sender.sendMessage("Shh!");
    }


    @WithParser(value = IntParser.class, priority = 10)
    @SubCommand("lol <int>")
    public void lol(TestSender sender, int times) {
        sender.sendMessage("Lol x" + times);
    }

    @SubCommand("lol <string>")
    public abstract void lol(TestSender sender, String string);

    @ExceptionHandle(PermissionException.class)
    public void noPermission(TestSender sender, PermissionException permissionException) {
        sender.sendMessage("You got no permissions to run this command you fool!");
    }

    @Override
    public Map<String, String> meow() {
        return Map.of(
                "meow", "test0",
                "woof", "test1"
        );
    }
}
