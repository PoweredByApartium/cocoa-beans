# 🧬 Polymorphism commands

**Table of content:**
- [Introduction](#introduction)
- [How can we use it](#way-it-can-be-used)
- [Making gamemode command](#example-of-using-polymorphism-commands)

<br id="introduction"/>
[🧬 Polymorphism](https://en.wikipedia.org/wiki/Polymorphism_(computer_science)) commands are way to make the process of writing commands easier and avoid code duplication.
At also provide easier way to edit the syntax of group of commands.

## Way it can be used

**TODO Voigon Write here**
points:
 - interfaces
 - abstract class
 - just general polymorphism

## Example of using Polymorphism commands

**TODO Voigon Write here**
```java
@Permission("net.apartium.cocoabeans.commands.gamemode.self")
/* package-private */ class GamemodeCommandBase implements CommandNode {
    
    protected setGamemode(CommandSender sender, Player target, GameMode gamemode) {
        target.setGamemode(gamemode);
        
        if (sender != target) {
            sender.sendMessage(target.getName() + " Gamemode have been set to " + cleanName(gamemode));
        }
        
        target.sendMessage("Your Gamemode have been set to " + cleanName(gamemode));
        
    }

    private String cleanName(GameMode gamemode) {
        return gamemode.name().subString(0, 1).toUpperCase() + gamemode.name().subString(1).toLowerCase();
    }
    
}
```

**TODO Voigon Write here**

```java
/* package-private */ class SpecificGamemodeCommandBase extends GamemodeCommandBase {
    
    private final GameMode gamemode;
    
    public SpecificGamemodeCommandBase(GameMode gamemode) {
        this.gamemode = gamemode;
    }
    
    @SenderLimit(SenderType.PLAYER)
    @SubCommand
    public void selfSetGamemode(Player sender) {
        setGamemode(sender, sender, gamemode);
    }

    @Permission("net.apartium.cocoabeans.commands.gamemode.other")
    @SubCommand("<player>")
    public void otherSetGamemode(CommandSender sender, Player target) {
        setGamemode(sender, target, gamemode);
    }
    
}
```

**TODO Voigon Write here**

<tabs>
<tab title="Survival">

```java
@Command(value = "gms", aliases = {"gm0"})
public class GamemodeSurvivalCommand extends SpecificGamemodeCommandBase {

    public GamemodeSurvivalCommand() {
        super(GameMode.SURVIVAL);
    }

}
```

</tab>
<tab title="Creative">

```java
@Command(value = "gmc", aliases = {"gm1"})
public class GamemodeCreativeCommand extends SpecificGamemodeCommandBase {

    public GamemodeCreativeCommand() {
        super(GameMode.CREATIVE);
    }

}

```

</tab>
<tab title="Adventure">

```java
@Command(value = "gma", aliases = {"gm2"})
public class GamemodeAdventureCommand extends SpecificGamemodeCommandBase {

    public GamemodeAdventureCommand() {
        super(GameMode.ADVENTURE);
    }

}
```

</tab>
<tab title="Spectator">

```java
@Command(value = "gmsp", aliases = {"gm3"})
public class GamemodeSpectatorCommand extends SpecificGamemodeCommandBase {

    public GamemodeSpectatorCommand() {
        super(GameMode.SPECTATOR);
    }

}
```

</tab>
</tabs>

**TODO Voigon Write here**
```java
@Command(value = "gamemode", aliases = {"gm"})
public class GamemodeModeCommand extends GamemodeCommandBase {

    @SenderLimit(SenderType.PLAYER)
    @SubCommand("<gamemode>")
    @SubCommand("<gamemode-int-value>")
    public void selfSetGamemode(Player sender, GameMode gamemode) {
        setGamemode(sender, sender, gamemode);
    }

    @Permission("net.apartium.cocoabeans.commands.gamemode.other")
    @SubCommand("<gamemode> <player>")
    @SubCommand("<gamemode-int-value> <player>")
    public void otherSetGamemode(CommandSender sender, Player target, GameMode gamemode) {
        setGamemode(sender, target, gamemode);
    }
    
}
```
