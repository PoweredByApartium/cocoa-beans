# 🔖 Named Argument

<sup>
Available Since 0.0.54
</sup>

**Table of content:**
- [Introduction](#introduction)
- [Usage](#usage)
  - [Annotations (quick & simple)](#annotations-quick-simple)
  - [Manual registration (recommended)](#manual-registration-recommended)
  - [Accessing named arguments](#accessing-named-arguments)
  - [Error handling](#error-handling)
  - [Tab completion](#tab-completion)
- [UnionCappableParser](#unioncappableparser)
- [Advantages](#advantages)

## Introduction
**Named arguments** let players specify command settings by name.</br>
Each setting describes its purpose, and players can supply only the settings they need, in any order within the command's named-argument group.

Cocoa Beans supports two kinds of named arguments:
- **options**: take a value, such as `--volume 10`
- **flags**: enable a behavior through their presence, such as `--loud`

Arguments can have short aliases (`-l`) and single-letter flags can be combined (`-lf`).

For example, a sound command could accept:
```
/sound --volume 10 --pitch 1.5 --loud
/sound --loud --pitch 1.5 --volume 10
/sound --pitch 1.5
```

The first two express the same settings.</br>
The third shows the biggest practical benefit: changing pitch does not require supplying volume first.

## Usage

There are two ways to declare named arguments: **annotations** for quick prototyping with a few items, and **manual registration** for full control. Manual registration is the recommended approach for most real commands.

### Annotations (quick & simple)

Annotations work well when you have a small number of simple flags or options and don't need requirements, default values, or shared references.</br>
However, they get ugly fast as the number of arguments grows -- each one needs its own annotation block with a keyword, and you must union them all with `+` in the `@SubCommand`.

`@WithOptionArgument` declares an option, `@WithFlagArgument` declares a flag. Both are repeatable.

```java
@WithOptionArgument(
        keyword = "volume",
        form = "v", aliases = "volume",
        usage = "Set volume",
        parser = IntParser.class
)
@WithFlagArgument(
        keyword = "loud-flag",
        form = "l", aliases = "loud",
        usage = "Set if loud"
)
@Command("sound")
public class SoundCommand implements CommandNode {

    @SubCommand("<volume+loud-flag>")
    public void sound(Sender sender, CommandContext context) {
        OptionArgumentValue<Integer> vol = context.option("volume");
        boolean loud = context.flag("loud");
        // ...
    }
}
```

The `+` syntax in `@SubCommand` unions the parsers together via `UnionCappableParser` (see [below](#unioncappableparser)).

### Manual registration (recommended)

For any real command, register a `NamedArgumentParser` directly. This gives you access to requirements, default values, value hints, and -- most importantly -- `static final` references for fast, type-safe access in your handler.

<tabs>

<tab title="SoundCommand.java">

```java
@Command("sound")
public class SoundCommand implements CommandNode {

    static final OptionArgument<Integer> VOLUME =
            NamedArgument.<Integer>option()
                    .form("v")
                    .forms("volume")
                    .usage("Set volume")
                    .parser(new IntParser())
                    .defaultValue(() -> 50)
                    .requirement(new PermissionRequirement("sound.volume"))
                    .build();

    static final OptionArgument<Float> PITCH =
            NamedArgument.<Float>option()
                    .form("p")
                    .forms("pitch")
                    .usage("Set pitch")
                    .parser(new FloatParser())
                    .valueHint("0.5-2.0")
                    .build();

    static final FlagArgument LOUD = NamedArgument.flag("l", "loud");

    @SubCommand("<sound-args>")
    public void sound(Sender sender, CommandContext context) {
        int volume = context.option(VOLUME).orDefault(); // 50 if not set
        float pitch = context.option(PITCH).orDefault();
        boolean loud = context.flag(LOUD);
        // ...
    }
}
```

</tab>

<tab title="Registration">

```java
NamedArgumentParser parser = new NamedArgumentParser(
        "sound-args",
        0,
        List.of(
                SoundCommand.VOLUME,
                SoundCommand.PITCH,
                SoundCommand.LOUD
        )
);

commandManager.registerArgumentTypeHandler(parser);
```

</tab>

</tabs>

The `OptionArgument.Builder` supports:

| Method                       | Description                                              |
|------------------------------|----------------------------------------------------------|
| `form(String)`               | Primary short form (e.g. `"v"`)                          |
| `forms(String...)`           | Long forms / aliases (e.g. `"volume"`)                   |
| `usage(String)`              | Human-readable description                               |
| `parser(ArgumentParser<T>)`  | The parser for the value                                 |
| `defaultValue(Supplier<T>)`  | Default value returned by `orDefault()` when not set     |
| `requirement(Requirement)`   | Requirement the sender must meet for this argument       |
| `valueHint(String)`          | Hint shown in usage (e.g. `"0.5-2.0"`)                  |

Named arguments can be mixed with positional arguments:
```
@SubCommand("<sound-args> test")   // named args before literal
@SubCommand("test <sound-args>")   // named args after literal
```

Players can then use the command in any of these ways:
```
/sound -v 50 -l --pitch 1.5
/sound --volume 50 --pitch 1.5 --loud
/sound --pitch 1.5
/sound -l
```

Single-letter flags can be combined: `-lp 1.5` is equivalent to supplying `-l` and `-p 1.5` separately.

### Accessing named arguments

Use `CommandContext` to retrieve parsed values:

| Method                           | Returns                  | Description                                |
|----------------------------------|--------------------------|--------------------------------------------|
| `context.option(VOLUME)`         | `OptionArgumentValue<T>` | Get option by reference (fast)             |
| `context.option("volume")`       | `OptionArgumentValue<T>` | Get option by form or alias (lookup)       |
| `context.flag(LOUD)`             | `boolean`                | Check flag by reference (fast)             |
| `context.flag("loud")`           | `boolean`                | Check flag by form or alias (lookup)       |
| `context.options()`              | `Set<OptionArgumentValue>` | All parsed options                       |
| `context.flags()`                | `Set<FlagArgument>`      | All parsed flags                           |

`OptionArgumentValue<T>`:

| Method         | Returns       | Description                                               |
|----------------|---------------|-----------------------------------------------------------|
| `isSet()`      | `boolean`     | Whether the player supplied this option                   |
| `orNull()`     | `T` or `null` | The parsed value, or `null` if not supplied               |
| `orDefault()`  | `T` or `null` | The parsed value, or the default if one is defined        |
| `get()`        | `Optional<T>` | The parsed value wrapped in an `Optional`                 |
| `raw()`        | `String`      | The raw string value before parsing                       |

### Error handling

When a named argument fails to parse, an `InvalidNamedArgumentException` is thrown.</br>
Handle it with `@ExceptionHandle` for user-friendly messages:

```java
@ExceptionHandle(InvalidNamedArgumentResponse.InvalidNamedArgumentException.class)
public void invalidNamedArgument(Sender sender,
        InvalidNamedArgumentResponse.InvalidNamedArgumentException e) {
    if (e.getReason() instanceof AmbiguousMappedKeyResponse ambiguity) {
        sender.sendMessage("Which one did you mean? ["
                + String.join(", ", ambiguity.getKeys()) + "]");
        return;
    }
    if (e.getReason() instanceof NoSuchElementInMapResponse noSuch) {
        sender.sendMessage("No such value: \"" + noSuch.getAttempted() + "\"");
        return;
    }
    sender.sendMessage(e.getMessage());
}
```

Errors handled automatically:
- Missing value (e.g. `-v` with no number): `"Missing value for v"`
- Unknown argument (e.g. `--unknown`): `"There is no --unknown named argument"`
- Duplicate (e.g. `-l -l`): `"l was given twice."`

### Tab completion

Named arguments support tab completion out of the box.

- When the current token starts with `-` or `--`, the system suggests available flags and options in `--form` form that haven't been used yet.
- When no `-` prefix is present, the system falls back to showing labels or tab completions from other argument parsers registered on the command.
- Once an option form has been typed (e.g. `--volume`), tab completion delegates to that option's underlying parser to suggest values.

## UnionCappableParser

`UnionCappableParser<T>` is the interface that enables combining multiple parsers of the same type into one. This is the mechanism behind the `+` syntax in `@SubCommand`.

```java
public interface UnionCappableParser<T> {
    ArgumentParser<T> union(ArgumentParser<?>... parsers);
}
```

### How it works

When you write `<volume+loud-flag+pitch>`, the command lexer splits by `+`, looks up each parser, and calls the first parser's `union()` with the rest. The result is a single merged parser.

For `NamedArgumentParser`, `union()` merges all named arguments into one combined parser:

```java
// Internally, NamedArgumentParser.union() does:
ArgumentParser<NamedArgumentResult> union(ArgumentParser<?>... parsers) {
    Set<NamedArgument> merged = new HashSet<>(this.namedArguments);
    for (ArgumentParser<?> parser : parsers) {
        merged.addAll(((NamedArgumentParser) parser).namedArguments);
    }
    return new NamedArgumentParser(getKeyword(), getPriority(), merged);
}
```

This is what annotations rely on -- each `@WithOptionArgument` / `@WithFlagArgument` creates a separate `NamedArgumentParser`, and `+` unions them. With manual registration, you create a single parser with all arguments already included, so no union is needed.

### Implementing your own

If you build a custom parser that should support `+` composition, implement `UnionCappableParser<T>`:

```java
public class MyParser extends ArgumentParser<MyResult>
        implements UnionCappableParser<MyResult> {

    @Override
    public ArgumentParser<MyResult> union(ArgumentParser<?>... parsers) {
        // Merge state from all parsers into a new combined instance
        return new MyParser(/* merged state */);
    }
}
```

All parsers joined by `+` must implement `UnionCappableParser`, otherwise an `IllegalArgumentException` is thrown at registration time.

## Advantages
- **Readable commands**: Names such as `--volume` make the meaning of a value visible.
- **Independent optional settings**: Players choose the settings they need without remembering order or supplying placeholders.
- **Convenient shortcuts**: Long names offer clarity while aliases and combined flags reduce typing.
- **Less boilerplate**: Declare which arguments a command supports without manual parsing or separate variants for every combination.
- **Composable**: Named argument parsers merge through `UnionCappableParser`, so they can be combined freely.
- **Existing parsers remain useful**: Option values use ordinary argument parsers -- any `ArgumentParser<T>` works out of the box.