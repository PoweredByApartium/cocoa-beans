package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.multilayered.Permission;

@Permission("perm.help.class")
@Description("class level permission command")
@Command(value = "classperm", aliases = "cp")
public class ClassPermissionHelpMenuCommand implements CommandNode {

    public HelpMenu helpMenu;

    @Description("first sub command")
    @SubCommand("first")
    public void first() {

    }

    @Description("second sub command")
    @SubCommand("second")
    public void second() {

    }

    @CommandDocs.Hidden
    @SubCommand("help")
    public void help(Sender sender, HelpMenu helpMenu) {
        this.helpMenu = helpMenu;
    }

}
