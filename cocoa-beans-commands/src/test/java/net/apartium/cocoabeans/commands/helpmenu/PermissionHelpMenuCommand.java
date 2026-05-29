package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.multilayered.Permission;

@Description("permission gated help command")
@Command("permhelp")
public class PermissionHelpMenuCommand implements CommandNode {

    public HelpMenu helpMenu;

    @Description("anyone can run this")
    @SubCommand("public")
    public void publicCmd() {

    }

    @Permission("perm.help.admin")
    @Description("only admins can run this")
    @SubCommand("admin")
    public void admin() {

    }

    @Permission("perm.help.staff")
    @Description("only staff can run this")
    @SubCommand("staff")
    public void staff() {

    }

    @CommandDocs.Hidden
    @SubCommand("help")
    public void help(Sender sender, HelpMenu helpMenu) {
        this.helpMenu = helpMenu;
    }

}
