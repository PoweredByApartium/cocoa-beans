# 🗃️ Source parser

**Table of content:**
- [Introduction](#introduction)
- [What is source parser](#what-is-source-parser)
- [Usage](#usage)
- [When to use it](#where-should-you-use-source-parser)

<br id="introduction"/>
Source parser are simple way to create parser

## What is source parser
Source parser are above function that return Map of `String` as key and `Object` as value obj could be anything You can just return instead of `Object` an enum value `Map<String, GameMode>`
<br/>Source parser are just an simple way to create key value peer parser
<br/>Also source parser have 4 arguments 2 of them are most have
<br/><sub>* is for must have argument</sub>
* `keyword`* The Keyword that triggers the parser
* `priority` (default 0) Parser priority
* `clazz`* The return type of the parser
* `resultMaxAgeInMills` (default 0) The max age of the result in milliseconds
* `ignoreCase` (default false) If it will check if it equalIgnore case instead just equal
* `lax` (default false) Whether or not it lazy mapping `(Lazy mapping will try to auto complete your label)`

### More about priority
If we have multiple parser not with the same keyword but for our example
`IntParser` & `StringParser`
We will want `IntParser` to try to parse first so his priority will be higher then `StringParser`

#### More about resultMaxAgeInMills
When the result is older it will be discarded the next time someone use the parser
And if you set it to `0` it will always be discarded
But if you want the result to never be discarded for example enum class
You just set it to `-1`

### More about lax
Lax is Lazy mapping but what is Lazy mapping?

Lazy mapping will try to autofill your command for example if you type
```
/gamemode c
```
And lax is on it will complete it to
```
/gamemode creative
```
If there is a collision like
```
/gamemode s
```
It could be both `survival` & `specator`
So in that case it would ignore it and throw an exception it could be handle with `@ExceptionHandle`


## Usage
<tabs>
<tab title="RandomCommand.java">

```java
@Command("random-command")
public class RandomCommand implements CommandNode {

    // result max age in mills set to -1 because enum shouldn't be 
    // change on runtime it should be one time calculation
    @SourceParser(
            keyword = "stage",
            clazz = GameStage.class,
            resultMaxAgeInMills = -1
    )
    public Map<String, GameStage> getGameStages() {
        return Arrays.stream(GameStage.values())
                .collect(Collectors.toMap(
                    value -> value.name.toLowerCase(),
                    value -> value
            ));
    }
    
    // We could use it like so
    @SubCommand("set stage <stage>")
    public void setStage(Sender sender, GameStage stage) {
        // Do something
    }

}

```

</tab>
<tab title="GameStage.java">

```java
public enum GameStage {
    WAITING,
    STARTING,
    ON_GOING,
    ENDING,
    CLOSING,
    FAILED,
    UNKNOWN
}
```

</tab>
</tabs>

## Where should you use Source parser
Source parser should be used when you key value peer as
- list of player
- list of materials
- etc