# 👁️ Visibility Api


**Table of content:**
 - [Introduction](#introduction)
 - [Using API](#registering-and-using-the-api)
 - [Visibility group](#visibility-group)
 - [Relevant links](#relevant-links)


<a id="introduction"></a>
<br/>

The visibility API works on top of Bukkit's existing hidePlayer and showPlayer apis and provide a simpler way to use it for many cases.
The visibility API works by a concept of groups, each group:
- Has its own members
- Can see specific groups
- Cannot see specific groups

Recommended use cases:
* Multiple games in one server, `where players can only see their game`
* Spectators, `Can see players and themselves but not vice-versa`
* Vanish, `Can see other players but players shouldn't have the abilty to see them`
* Each world has it's own players (For large open world servers), `Every world can only see it's own players`
## Registering and using the API

Each server should have only **one manager**
could be create like so we will probably want to also register visibility manager
```java
public final class CocoaBeansSpigotLoader extends JavaPlugin {

    private VisibilityManager visibilityManager;
    
    @Override
    public void onEnable() {
        visibilityManager = new VisibilityManager(this);
        
        // Register listener
        visibilityManager.registerListener(); // You could also pass as a parameter VisibilityPlayerRemoveType as default it's VisibilityPlayerRemoveType.ON_LEAVE
    }

}

```

When we call the function `VisibilityManager#registerListener` it will have `VisibilityPlayerRemoveType` of `ON_LEAVE`,

There are two types of `VisibilityPlayerRemoveType`
 - **ON_LEAVE** `Remove the player from in memory state when they quit`
 - **NEVER** `When the player will re-join the visibility policies will be automatically applied from the same group, but it can cause a memory leak (if we never clear it)`

When we use `VisibilityPlayerRemoveType.NEVER` we will want to handle removal by our self
We could get all players in memory by calling the function `VisibilityManager#getPlayers` and stream to filter which players should be removes

## Visibility group

First, we will want to create group with `VisibilityManager#getOrCreateGroup` 
Groups are identified by their string id, if we invoke the method with the same param on the same instance we will receive the same group (identity is guaranteed).
The following method creates a group for all players in the game. It means that the players in the game will only see each other and not other players on the server. 
```java
public void addGame(VisibilityManager visibilityManager, Game game) {
    VisibilityGroup group = visibilityManager.getOrCreateGroup("skywars-" + game.getUniqueId());
    
    for (Player target : game.getPlayers()) {
        gruop.addPlayer(target);
    }
}
```

When the game is over we will probably want to delete all the groups we have created to avoid memory leaks. 
```java
public void deleteGame(VisibilityManager visibilityManager, Game game) {
    visibilityManager.deleteGroup("skywars-" + game.getUniqueId());
    visibilityManager.deleteGroup("skywars-" + game.getUniqueId() + "-spectator");
}
```

When a player is removed from the game 
```java
public void removePlayerFromGame(VisibilityManager visibilityManager, Game game, Player target) {
    VisibilityGroup group = visibilityManager.getOrCreateGroup("skywars-" + game.getUniqueId());
    group.removePlayer(target);
}
```

Initialize a spectator group. Players on the spectator group will be able to see players on the main group, but not vice versa. 
```java
public void addSpectatorToGame(VisibilityManager visibilityManager, Game game) {
    VisibilityGroup mainGroup = visibilityManager.getOrCreateGroup("skywars-" + game.getUniqueId());
    VisibilityGroup spectatorGroup = visibilityManager.getOrCreateGroup("skywars-" + game.getUniqueId() + "-spectator");

    // players on the spectators group can see players on the main group
    spectatorGroup.addVisibleGroup(mainGroup);
    
    // players on the main group cannot see players on the spectator group
    mainGroup.addHiddenGroup(spectatorGroup);
}
```

Initialize a group of vanished admins, who can be seen by no-one except themselves. 
```java
public void addVanishGroup(VisibilityManager visibilityManager) {
    VisibilityGroup vanishGroup = visibilityManager.getOrCreateGroup("vanish");

    // TODO add players to vanish group we will want to be vanish
    
    for (VisibilityGroup group : vanishGroup.getGroups()) {
        if (vanishGroup == group)
            continue;
        
        group.addHiddenGroup(vanishGroup);
    }
}
```

## Relevant Links
* [javadocs](https://cocoa-beans.apartium.net/%version%/spigot/net/apartium/cocoabeans/spigot/visibility/package-summary.html)
