/*
 * Copyright (c) 2026, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.parsers.WithParser;
import net.apartium.cocoabeans.commands.spigot.parsers.exception.NoSuchWorldTypeResponse;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;

@Command("worldtype")
@WithParser(WorldTypeParser.class)
public class WorldTypeCommand implements CommandNode {

    @SubCommand("<world-type>")
    public void world(CommandSender sender, WorldType worldType) {
        sender.sendMessage("world type: " + worldType.getName());
    }

    @ExceptionHandle(NoSuchWorldTypeResponse.NoSuchWorldTypeException.class)
    public void noSuchWorld(CommandSender sender, NoSuchWorldTypeResponse.NoSuchWorldTypeException exception) {
        sender.sendMessage("No world type by the name of " + exception.getAttempted());
    }
}
