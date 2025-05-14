package net.apartium.cocoabeans.spigot.commands;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;

@SenderLimit(SenderType.PLAYER)
@Command(value = "cocoaboard", aliases = "cb")
public class CocoaBoardCommand implements CommandNode {

}
