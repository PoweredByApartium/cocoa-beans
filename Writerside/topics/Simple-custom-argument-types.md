# 📝 Simple custom argument types

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