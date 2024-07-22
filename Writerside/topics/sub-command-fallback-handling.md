# 🔬 Sub command fallback handling

NOTE: Fallback can be replaced by using [🪤 Exception handling](sub-command-fallback-handling.md)

## Deprecated
there is new way to work with error handling but this way still work
we will write about it soon so keep your eyes 👁️ on the wiki

Error handling is done via parser priorities. As a best practice you should specify priority for fallback sub commands as negative integers. In the following example, there are 3 fallbacks:

* One with priority of -5, which is called if more than one param is executed

* One for non-players, if no argument is specified

* One for when a target is specified, but it is not found

```Java
@Command("ping")
public class PingCommand implements CommandNode {

    private String getColorPing(int ping) {
        if (ping <= 20) return "§a" + ping;
        if (ping <= 70) return "§2" + ping;
        if (ping <= 150) return "§c" + ping;
        return "§4" + ping;
    }

    // player check ping for himself
    @SenderLimit(SenderType.PLAYER) // allow only players to run this sub command
    @SubCommand
    public void selfPingCheck(Player player) {
        player.sendMessage("§eYour ping is " + getColorPing(player.getPing()) + "ms");
    }

    // Non-players cannot check ping for themselves, must specify a player
    @SenderLimit(value = SenderType.PLAYER, invert = true) // allow anyone BUT player to run this sub command
    @SubCommand
    public void usages(CommandSender sender, CommandContext context) {
        sender.sendMessage("§cUsage: /" + context.commandName() + " <player>");
    }

    // this acts as fallback, priority -5 means that it will be called last if no other parser can parse the argument
    // this command will be called when more than one argument is provided because StringsParser is used
    @WithParser(value = StringsParser.class, priority = -5)
    @SubCommand("<strings>")
    public void usage(CommandSender sender, CommandContext context) {
        usages(sender, context);
    }

    // called when a player arg is provided
    @SubCommand("<player>")
    public void pingCheck(CommandSender sender, Player target) {
        sender.sendMessage("§e" + target.getName() + " ping " + getColorPing(target.getPing()) + "ms");
    }

    // called when a single argument is provided but player is not found
    @WithParser(value = StringParser.class, priority = -1)
    @SubCommand("<ignore>")
    public void pingCheckNotFound(CommandSender sender) {
        sender.sendMessage("§cPlayer not found!");
    }
}
```