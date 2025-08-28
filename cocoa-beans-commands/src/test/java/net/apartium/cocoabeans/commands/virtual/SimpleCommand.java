package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Description;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.multilayered.Permission;

@Permission("meow")
@Description("A simple description")
@Command(value = "simple")
public class SimpleCommand implements CommandNode {

    @SubCommand("set <int>")
    public void set() { }

    @Description("Variant that clear stuff")
    @Permission("my.permission")
    @SubCommand("clear")
    public void clear() { }

    @SubCommand("<string>")
    public void everString() { }

}
