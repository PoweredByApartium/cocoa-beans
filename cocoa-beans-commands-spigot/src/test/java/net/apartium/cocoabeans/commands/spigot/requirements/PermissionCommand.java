package net.apartium.cocoabeans.commands.spigot.requirements;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.spigot.exception.PermissionException;
import org.bukkit.command.CommandSender;

@Command("permission")
public class PermissionCommand implements CommandNode {

    @Permission("my.test")
    @SubCommand("test")
    public void testWithPermission(CommandSender sender) {
        sender.sendMessage("You got a permission");
    }

    @Permission(value = "my.test", invert = true)
    @SubCommand("test")
    public void testWithoutPermission(CommandSender sender) {
        sender.sendMessage("You don't have a permission");
    }

    @Permission(value = "my.test", invert = true)
    @SubCommand("wow")
    public void testWithAndWithoutPermission(CommandSender sender) {
        sender.sendMessage("nah");
    }

    @ExceptionHandle(PermissionException.class)
    public void noPermission(CommandSender sender, PermissionException permissionException) {
        sender.sendMessage(permissionException.getMessage());
    }


}
