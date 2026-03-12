package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import org.bukkit.command.CommandSender;

@Command("dog")
public class DogCommand implements CommandNode {

    public record SenderContainer(CommandSender commandSender) {}

    public record StringContainer(String value) {}

    @SubCommand("set <string>")
    public void setString(CommandSender sender, StringContainer string) {
        sender.sendMessage("value is " + string.value());
    }

    @SubCommand("remove <string>")
    public void removeString(SenderContainer sender, StringContainer string) {
        sender.commandSender().sendMessage("value is " + string.value());
    }

}
