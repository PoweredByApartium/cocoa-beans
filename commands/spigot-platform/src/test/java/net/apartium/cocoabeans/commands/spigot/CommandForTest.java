package net.apartium.cocoabeans.commands.spigot;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.OptionalInt;

@Command("test")
public class CommandForTest implements CommandNode {

    @SubCommand
    public void noArgs(CommandSender sender) {
        sender.sendMessage("No args");
    }

    @SubCommand("cool")
    public void cool(CommandSender sender) {
        sender.sendMessage("Cool!");
    }

    @SubCommand("tp <player> <player>")
    public void teleportToOther(CommandSender sender, Player from, Player to) {
        from.teleport(to.getLocation());
        sender.sendMessage("Teleporting " + from.getName() + " to " + to.getName());
    }

    @SubCommand("give <player> <material> <?int>")
    public void give(CommandSender sender, Player player, Material material, OptionalInt amount) {
        if (amount.isPresent() && amount.getAsInt() <= 0) {
            sender.sendMessage("Invalid amount");
            return;
        }

        player.getInventory().addItem(new ItemStack(material, amount.orElse(1)));
        player.sendMessage("Given " + amount.orElse(1) + " " + material.name() + " to " + player.getName());
    }

    @SubCommand("location <location>")
    public void location(CommandSender sender, Location location) {
        sender.sendMessage("Is that your location? " + location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ());
    }

}
