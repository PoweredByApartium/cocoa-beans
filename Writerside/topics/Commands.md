# The command system

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
@Command("reboot") // this is the label of the command, in minecraft <label>
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

## Example with advanced error handling
Error handling is done via parser priorities. As a best practice you should specify priority for fallback sub commands as negative integers. 
In the following example, there are 3 fallbacks:
- One with priority of -5, which is called if more than one param is executed
- One for non-players, if no argument is specified
- One for when a target is specified, but it is not found
```java
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

## Example with integer arguments
```java
@Command("amount")
public class AmountCommand implements CommandNode {

    // called if one param is provided, but it's not a number
    @WithParser(value = DummyParser.class, priority = -1)
    @SubCommand("<ignore>")
    public void invalidNumber(CommandSender sender) {
        sender.sendMessage("§cInvalid number!");
    }

    // called when a single numerical arg is provided
    @SubCommand("<int>")
    public void amount(CommandSender sender, int num) {
        if (num <= 0) {
            sender.sendMessage("§cNumber must be bigger than zero");
            return;
        }

        int stacks = num / 64;
        int remaining = num % 64;

        StringBuilder sb = new StringBuilder();
        if (stacks != 0) sb.append("§c").append(stacks).append(" §estack").append(stacks != 1 ? "s " : " ");
        if (remaining != 0) sb.append("§c").append(remaining).append(" ");


        sender.sendMessage("§c" + num + " §eis " + sb.substring(0, sb.length() - 1));
    }

    // called when more than one argument is provided
    @SubCommand
    @SubCommand("<strings>")
    public void usage(CommandSender sender, CommandContext context) {
        sender.sendMessage("§cUsage: /" + context.commandName() + " <amount>");
    }

}
```
## Example with simple custom argument types
Cocoa beans provides a simple way of declaring custom argument types, however, the following method is not reusable across commands. 
If you want a reusable parser, head over to the [Advanced reusable parsers](#advanced-reusable-parsers) section to see how a parser with similar functionality can be created.
```java
@Command("lastseen")
public class LastSeenCommand implements CommandNode {

    private final PlayerManager playerManager;
    
    public LastSeenCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    // this is the parser option, it can be used for tab completion as well as for parsing arguments
    @SourceParser(keyword = "playerData", clazz = PlayerData.class)
    public Map<String, PlayerData> of() {
        return playerManager.getPlayerDataMap().values().stream()
                .map(playerData -> Map.entry(playerData.getUsername(), playerData))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @SubCommand("<playerData>")
    public void lastSeen(CommandSender sender, PlayerData playerData) {
        if (playerData.getLastSeen() == null) {
            sender.sendMessage("§cPlayer hasn't have last seen");
            return;
        }

        sender.sendMessage("§7Last seen: " + playerData.getLastSeen());
    }

}
```
## Advanced reusable parsers
You can interact with the command system at a low level by implementing the ArgumentParser class.
```java
public class PlayerDataParser extends ArgumentParser<PlayerData> {

    PlayerManager playerManager;

    public PlayerDataParser(PlayerManager playerManager) {
        this(playerManager, "playerData", 0);
    }

    public PlayerDataParser(PlayerManager playerManager, String keyword) {
        this(playerManager, keyword, 0);
    }

    public PlayerDataParser(PlayerManager playerManager, int priority) {
        this(playerManager, "playerData", priority);
    }

    public PlayerDataParser(PlayerManager playerManager, String keyword, int priority) {
        super(keyword, PlayerData.class, priority);
        
        this.playerManager = playerManager;
        
    }

    @Override
    public Optional<ParseResult<PlayerData>> parse(CommandProcessingContext commandProcessingContext) {
        int index = commandProcessingContext.index(); // index of the current argument to be processed
        List<String> args = commandProcessingContext.args(); // in bukkit, this is usually seen as String[] args

        // return if no argument is provided to this parser
        if (index >= args.size())
            return Optional.empty();

        // retrieve player data from memory
        PlayerData playerData = playerManager.getPlayerData(args.get(index));
        if (playerData == null)
            return Optional.empty(); // if argument is not found, return empty

        return Optional.of(new ParseResult<>(
            playerData, // a non-null result
            index + 1 // the index of the next argument to be processed
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext commandProcessingContext) {
        // tryParse is used for implementing tab completions, this detects if this parser is suitable to handle request argument
        return parse(commandProcessingContext)
                .map(ParseResult::newIndex)
                .map(OptionalInt::of)
                .orElse(OptionalInt.empty());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext commandProcessingContext) {
        int index = commandProcessingContext.index();
        List<String> args = commandProcessingContext.args();

        // return if no argument is provided to this parser
        if (index > args.size())
            return Optional.empty();
        
        if (index == args.size()) // called if the player did not start writing the current argument
            return Optional.of(new TabCompletionResult(
                    playerManager.getPlayerNames(),
                    index + 1)
            );
        else  // called if player is in the middle of writing the current argument, so we have to look for matches
            return Optional.of(new TabCompletionResult(
                playerManager.getPlayerNames().stream()
                        .filter(s -> s.toLowerCase().startsWith(args.get(index).toLowerCase()))
                        .collect(Collectors.toSet()),
                index + 1)
            );
    }
}
```

And now, we have to register the parser in the command system:
```java
SpigotCommandManager commandManager = new SpigotCommandManager(this);

// no parsers are included by default, so we need to add some of our own
commandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
commandManager.registerArgumentTypeHandler(SpigotCommandManager.SPIGOT_PARSERS);

commandManager.registerArgumentTypeHandler(new PlayerDataParser(playerManager));
```
