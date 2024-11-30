# 🔬 Compound parser

<sup>
Available Since 0.0.37
</sup>

**Table of content:**
- [Introduction](#introduction)
- [Usage](#usage)
- [Advantages](#advantages)

## Introduction
Writing custom parsers for complicated arguments, such as locations, can be a tedious and error prone task.
For this reason we have added a way to define compound parsers in the same way its possible to define commands. Each parser could have multiple variants with different input types.

## Usage
We will create a simple compound parser called `LocationParser` that will parse a location from a string doubles,
We will also have a variant with yaw & pitch as float
<tabs>

<tab title="LocationParser.java">


```java
// We need to provide all of our parsers that we will want to use
@WithParser(DoubleParser.class)
@WithParser(FloatParser.class)
@WithParser(WorldParser.class)
public class LocationParser extends CompoundParser<Location> {
    
    public static final String DEFAULT_KEYWORD = "location";
    
    public LocationParser(int priority, String keyword) {
        super(
          keyword, // We need to provide a keyword
          Location.class, // We need to provide a type of the result
          priority, // We need to provide a priority of the parser
          new SimpleArgumentMapper(), // argument mapper to map arguments
          new SimpleCommandLexer()// command lexer to tokenize the variant
        );
    }

    public LocationParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    // Simple variant
    @ParserVariant("<world> <double> <double> <double>")
    public Location parseWorldWithXyz(World world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }

    // another variant with yaw & pitch
    @ParserVariant("<world> <double> <double> <double> <float> <float>")
    public Location parseWorldWithXyzYawPitch(World world, double x, double y, double z, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    // We could also have a requirement
    @SenderLimit(SenderType.PLAYER)
    @ParserVariant("<double> <double> <double>")
    public Location parseWithXyz(Player sender, double x, double y, double z) {
        return new Location(
                sender.getWorld(),
                x,
                y,
                z
        );
    }

    @SenderLimit(SenderType.PLAYER)
    @ParserVariant("<double> <double> <double> <float> <float>")
    public Location parseWithXyzYawPitch(Player sender, double x, double y, double z, float yaw, float pitch) {
        return new Location(
                sender.getWorld(),
                x,
                y,
                z,
                yaw,
                pitch
        );
    }
}
```

</tab>

</tabs>

That is everything you need to do to create your own compound parser.
There are a couple of things to note:
 - You need to provide all of your parsers that you will want to use
 - You may want to create a wrapper class that will wrap your compound parser and will cache the variants so you don't have to create them every time someone want to change keyword/priority

#### How could we warp it?

We will change `LocationParser` to be called `LocationParserImpl` and put it as `/* package-private */` then we will create class `LocationParser` that will be public
```java
public class LocationParser extends ArgumentParser<Location> {
    
    private static LocationParserImpl impl = new LocationParserImpl();

    public static final String DEFAULT_KEYWORD = "location";

    public LocationParser(int priority, String keyword) {
        super(
                keyword,
                Location.class,
                priority
        );
    }
    
    public LocationParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        return impl.parse(processingContext);
    }
    
    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return impl.tryParse(processingContext);
    }
    
    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return impl.tabCompletion(processingContext);
    }

}
```
That will save you memory and computation time but the parsers you use must support multithreading because there could be multiple threads running at the same time, and you don't want to create a new instance of the parser every time.
You could lock the parser but it's not recommended because it will slow down the parsing process.

## Advantages
 - **Improved Readability** 📖: The compound parser provides a clear and descriptive name for each variant, making the command structure more readable and self-explanatory.
- **Easier to Use** 👍: The compound parser is easy to use and can be used to create complex parsers with multiple parameters and variants with different requirements.
- **Reduced Code** 📦: The compound parser is a reusable component that can be used in multiple commands to avoid code duplication and reduce the amount of code needed to write a command.
- **Increased Flexibility** 🎯: The compound parser allows you to create parsers that are more complex and can be used in multiple places in your command structure.
- **Reduced Error Prone** 🐛: The compound parser is easy to use and can be used to create complex parsers with multiple parameters and variants with different requirements.
