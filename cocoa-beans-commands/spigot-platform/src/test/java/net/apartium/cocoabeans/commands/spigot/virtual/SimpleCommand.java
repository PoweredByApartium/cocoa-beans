package net.apartium.cocoabeans.commands.spigot.virtual;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.spigot.requirements.Permission;

@Permission("meow")
@Command(value = "simple")
public class SimpleCommand implements CommandNode {

    @SubCommand("set <int>")
    public void set() { }

    @Permission("my.permission")
    @SubCommand("clear")
    public void clear() { }

    @SubCommand("<string>")
    public void everString() { }

}

