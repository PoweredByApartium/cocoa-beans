package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.parsers.WithParser;
import net.apartium.cocoabeans.commands.spigot.parsers.exception.NoSuchWorldResponse;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

@WithParser(WorldParser.class)
@Command("world")
public class WorldCommand implements CommandNode {

    @SubCommand("<world>")
    public void world(CommandSender sender, World world) {
        sender.sendMessage("world: " + world.getName());
    }

    @ExceptionHandle(NoSuchWorldResponse.NoSuchWorldException.class)
    public void noSuchWorld(CommandSender sender, NoSuchWorldResponse.NoSuchWorldException exception) {
        sender.sendMessage("No world by the name of " + exception.getAttempted());
    }

}
