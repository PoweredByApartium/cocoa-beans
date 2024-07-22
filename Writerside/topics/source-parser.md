# 🗃️ Source parser

**Table of content:**
- [Introduction](#introduction)
- [Explain about argument of source parser](#explain-meow)
- [Usage](#usage)
- [When to use it](#where-should-you-use-source-parser)

<br id="introduction"/>
Source parser are simple way to create parser

## Explain meow
Source parser are above function that return Map of `String` as key and `Object` as value obj could be anything You can just return instead of `Object` an enum value `Map<String, GameMode>`<br/>Also source parser have 4 arguments 2 of them are most have<br/>
<sub>* is for must have argument</sub>
* `keyword`* The Keyword that triggers the parser
* `priority` (default 0) Parser priority
* `clazz`* The return type of the parser
* `resultMaxAgeInMills` (default 0) The max age of the result in milliseconds

### More about priority
If we have multiple parser not with the same keyword but for our example
`IntParser` & `StringParser`
We will want `IntParser` to try to parse first so his priority will be higher then `StringParser`

#### More about resultMaxAgeInMills
When the result is older it will be discarded the next time someone use the parser
And if you set it to `0` it will always be discarded
But if you want the result to never be discarded for example enum class
You just set it to `-1`


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