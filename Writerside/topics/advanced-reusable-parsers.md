# 🔐 Advanced reusable parsers

<tip>
You can interact with the command system at a low level by implementing the ArgumentParser class.
</tip>

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