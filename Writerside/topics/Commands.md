# 💻 The command system

Cocoa beans includes its own Minecraft-compatible command system, to improve developer efficiency and reduce time wasted
on boilerplate code.

## Registering commands
<code-block lang="java">
SpigotCommandManager commandManager = new SpigotCommandManager(this);

// no parsers are included by default, so we need to add some of our own
commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
commandManager.registerArgumentTypeHandler(SpigotCommandManager.SPIGOT_PARSERS);

commandManager.addCommand(new RebootCommand(rebootManager));
</code-block>

## A simple command

This is how a relatively simple command looks like:

<code-block lang="java">
@Permission("example.admin.reboot") // this is the permission for the root command
@Command("reboot") // this is the label of the command, in minecraft &ltlabel&gt
public class RebootCommand implements CommandNode {

    private final RebootManager rebootManager;

    public RebootCommand(RebootManager rebootManager) {
        this.rebootManager = rebootManager;
    }

    @SubCommand // allow sub commands most be annotated with @SubCommand
    public void noArgsReboot(CommandSender sender) {
        // this variant will be called when no args are provided
        // ex /reboot
        sender.sendMessage("30 seconds");
        rebootManager.startReboot(30);
    }

    @SubCommand("<int>")
    public void rebootWithDelayArg(CommandSender sender, int seconds) {
        // this variant will be called when an integer argument is provided
        // ex /reboot 60
        boolean stared = rebootManager.startReboot(seconds);
        sender.sendMessage(stared ? "§aRestart has been started " + seconds + " seconds (yeah I'm lazy do add parser)" : "§cThere is already reboot");
    }

    @SubCommand("cancel")
    public void cancel(CommandSender sender) {
        // this command will be called "/reboot cancel" is executed
        boolean isCancelled = rebootManager.cancel();
        sender.sendMessage(isCancelled ? "§aReboot has been cancelled successful" : "§cThere is no reboot process that needed to be cancel");
    }

}
</code-block>

This command defines 3 sub commands:
- arg-less command, to reboot in 30 seconds
- command with an integer argument, to reboot in a specified amount of seconds
- a command to cancel the reboot process


