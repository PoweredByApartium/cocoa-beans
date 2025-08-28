package net.apartium.cocoabeans.commands.spigot;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandContext;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.exception.InvalidUsageResponse;
import net.apartium.cocoabeans.commands.spigot.exception.PermissionException;
import net.apartium.cocoabeans.commands.spigot.requirements.Permission;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
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

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("tp <player>")
    public void teleport(Player player, Player target) {
        player.teleport(target.getLocation());
        player.sendMessage("Teleporting " + player.getName() + " to " + target.getName());
    }

    @SubCommand("who <player>")
    public void whoIs(CommandSender sender, Player target) {
        sender.sendMessage("Who is " + target.getName() + "? " + target.getUniqueId());
    }

    @SubCommand("give <player> <material> <?int>")
    public void give(CommandSender sender, Player player, Material material, OptionalInt amount) {
        if (amount.isPresent() && amount.getAsInt() <= 0) {
            sender.sendMessage("Invalid amount");
            return;
        }

        player.getInventory().addItem(new ItemStack(material, amount.orElse(1)));
        sender.sendMessage("Given " + amount.orElse(1) + " " + material.name() + " to " + player.getName());
    }

    @SubCommand("location <location>")
    public void location(CommandSender sender, Location location) {
        sender.sendMessage("Is that your location? " + location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ());
    }

    @ExceptionHandle(value = InvalidUsageResponse.InvalidUsageException.class)
    public void invalidUsage(CommandSender sender, CommandContext context) {
        if (context == null || context.args().length != 2 && !context.args()[0].equalsIgnoreCase("who")) {
            sender.sendMessage("Invalid usage");
            return;
        }

        sender.sendMessage("idk who is " + context.args()[1]);
    }

    @ExceptionHandle(PermissionException.class)
    public void noPermission(CommandSender sender, PermissionException permissionException) {
        sender.sendMessage(permissionException.getMessage());
    }

    @Permission("cocoabeans.test")
    @SubCommand("permission")
    public void withPermission(CommandSender sender) {
        sender.sendMessage("You have permission!");
    }

}
