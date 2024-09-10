package net.apartium.cocoabeans.commands.spigot;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import org.bukkit.command.CommandSender;

@Command("senderlimit")
public class SenderLimitCommand implements CommandNode {

    @SenderLimit({SenderType.PLAYER, SenderType.CONSOLE})
    @SubCommand("whoAmI")
    public void whoAmI(CommandSender sender, SenderType senderType) {
        sender.sendMessage("I'm a " + senderType.name());
    }
}
