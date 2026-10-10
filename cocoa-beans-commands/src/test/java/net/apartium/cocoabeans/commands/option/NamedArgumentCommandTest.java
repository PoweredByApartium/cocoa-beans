package net.apartium.cocoabeans.commands.option;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.parsers.FloatParser;
import net.apartium.cocoabeans.commands.parsers.IntParser;
import net.apartium.cocoabeans.commands.parsers.exception.AmbiguousMappedKeyResponse;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidNamedArgumentResponse;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import net.apartium.cocoabeans.commands.parsers.option.WithOptionArgument;
import net.apartium.cocoabeans.commands.parsers.option.WithFlagArgument;

@WithOptionArgument(
        keyword = "volume",
        form = "v",
        aliases = "volume",
        usage = "Set volume",
        parser = IntParser.class
)
@WithOptionArgument(
        keyword = "pitch",
        form = "pitch",
        usage = "Set pitch",
        parser = FloatParser.class
)
@WithOptionArgument(
        keyword = "priority-argument",
        form = "p",
        aliases = "priority",
        usage = "Set priority",
        parser = Priority.Parser.class
)
@WithFlagArgument(
        keyword = "loud-flag",
        form = "l",
        aliases = "loud",
        usage = "Set if loud"
)
@WithFlagArgument(
        keyword = "fast-flag",
        form = "f",
        aliases = "fast",
        usage = "Set if fast"
)
@Command("named")
/* package-private */ class NamedArgumentCommandTest implements CommandNode {

    @SubCommand("<volume+loud-flag+pitch+fast-flag+priority-argument>")
    public void named(Sender sender, CommandContext context) {
        OptionArgumentValue<Integer> option = context.option("volume");
        sender.sendMessage(
                "volume: " + (option.isSet()
                        ? option.orNull()
                        : "none"
                )
        );

        boolean loud = context.flag("loud");
        sender.sendMessage("loud: " + loud);

        OptionArgumentValue<Float> pitch = context.option("pitch");
        sender.sendMessage(
                "pitch: " + (pitch.isSet()
                        ? pitch.orNull()
                        : "none"
                )
        );

        boolean fast = context.flag("fast");
        sender.sendMessage("fast: " + fast);

        OptionArgumentValue<Priority> priority = context.option("priority");
        sender.sendMessage(
                "priority: " + (priority.isSet()
                        ? priority.orNull()
                        : "none"
                )
        );
    }

    @SubCommand("<volume+loud-flag+pitch+fast-flag+priority-argument> test")
    public void named2(Sender sender, CommandContext context) {
        sender.sendMessage("test");
        named(sender, context);
    }

    @SubCommand("testB <volume+loud-flag+pitch+fast-flag+priority-argument>")
    public void named3(Sender sender, CommandContext context) {
        sender.sendMessage("test2");
        named(sender, context);
    }

    @ExceptionHandle(InvalidNamedArgumentResponse.InvalidNamedArgumentException.class)
    public void invalidNamedArgument(Sender sender, InvalidNamedArgumentResponse.InvalidNamedArgumentException response) {
        if (response.getReason() instanceof AmbiguousMappedKeyResponse ambiguity) {
            sender.sendMessage("Which one did you mean? [" + String.join(", ", ambiguity.getKeys()) + "]");
            return;
        }

        if (response.getReason() instanceof NoSuchElementInMapResponse noSuchElement) {
            if (noSuchElement.getParser().getClass() == Priority.Parser.class) {
                sender.sendMessage("No such priority: \"" + noSuchElement.getAttempted() + "\"");
                return;
            }
        }

        sender.sendMessage(response.getMessage());
    }

}
