package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.*;

@Description("wow this is command with help menu")
@Command("help")
public class HelpMenuCommand implements CommandNode {

    public HelpMenu helpMenu;

    @Description("this command get int")
    @SubCommand("test <int>")
    public void test(int num) {

    }

    @Description("this command get double")
    @SubCommand("test <double>")
    public void test(double num) {

    }

    @Description("this command don't take argument after test")
    @SubCommand("test")
    public void test() {

    }

    @CommandDocs.Hidden
    @SubCommand("help")
    public void help(Sender sender, HelpMenu helpMenu) {
        this.helpMenu = helpMenu;
    }

}
