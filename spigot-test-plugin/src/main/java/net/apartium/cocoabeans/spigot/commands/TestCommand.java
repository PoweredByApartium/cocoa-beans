package net.apartium.cocoabeans.spigot.commands;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@Command("test")
public class TestCommand implements CommandNode {

    @SubCommand("timer <duration>")
    public void timer(CommandSender sender, Duration duration) {
        sender.sendMessage("§6Timer has been set to: §c" + duration.toSeconds() + " seconds");
    }

}
