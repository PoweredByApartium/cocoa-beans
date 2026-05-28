package net.apartium.cocoabeans.commands.helpmenu;

import net.apartium.cocoabeans.commands.*;

import java.util.Optional;

@Description("rich help command with many features")
@LongDescription({"line1", "line2"})
@Usage("/richhelp <subcommand>")
@Command(value = "richhelp", aliases = {"rh", "rhelp"})
public class RichHelpMenuCommand implements CommandNode {

    public HelpMenu helpMenu;

    @CommandDocs.Section("system")
    @CommandDocs.Since("1.0.0")
    @CommandDocs.Id("rich.info")
    @Description("show info")
    @SubCommand("info")
    public void info() {

    }

    @CommandDocs.Hidden
    @Description("a secret subcommand that should not appear")
    @SubCommand("secret")
    public void secret() {

    }

    @Description("greet by name")
    @SubCommand("greet <string>")
    public void greet(String name) {

    }

    @Description("optional name")
    @SubCommand("hello <?string>")
    public void hello(Optional<String> name) {

    }

    @Description("case sensitive label")
    @SubCommand(value = "CASE", ignoreCase = false)
    public void caseSensitive() {

    }

    @Description("nested branch")
    @SubCommand("nested deep <int>")
    public void nested(int n) {

    }

    @CommandDocs.Hidden
    @SubCommand("help")
    public void help(Sender sender, HelpMenu helpMenu) {
        this.helpMenu = helpMenu;
    }

}
