package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import org.bukkit.command.CommandSender;

@Command("cat")
public class CatCommand implements CommandNode {

    @SubCommand("set <string> <meow>")
    public void setCat(CommandSender sender, String id, Meow cat) {
        sender.sendMessage(id + " has been set to " + cat);
    }

}
