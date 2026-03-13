package net.apartium.cocoabeans.spigot.commands;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@Command("test")
public class TestCommand implements CommandNode {

    @SubCommand("timer <duration>")
    public void timer(CommandSender sender, Duration duration) {
        sender.sendMessage("§6Timer has been set to: §c" + duration.toSeconds() + " seconds");
    }

    @SubCommand("timer <duration> <player>")
    public void timerTo(CommandSender sender, Duration duration, Player target) {
        sender.sendMessage("§6Timer has been set to: §c" + duration.toSeconds() + " seconds §6for §c" + target.getName());
        timer(target, duration);
    }

}
