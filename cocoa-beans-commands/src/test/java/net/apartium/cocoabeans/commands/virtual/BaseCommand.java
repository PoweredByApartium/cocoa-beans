package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Description;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.multilayered.Permission;

@VirtualMetadata(key = "test", value = "wow")
public abstract class BaseCommand implements CommandNode {

    @Permission("set.base")
    @SubCommand("set <int>")
    public abstract void set(int value);

    @SubCommand("clear")
    public abstract void clear();

    @Description("Get info")
    @SubCommand("get")
    public abstract void get();

}
