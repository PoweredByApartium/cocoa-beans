package net.apartium.cocoabeans.commands.optional;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.parsers.SourceParser;

import java.util.Map;
import java.util.Optional;

@Command("test")
public class OptionalCommandForTest implements CommandNode {

    @SubCommand("optional <?string> <?string> <?string>")
    public void optionals(Sender sender, Optional<String> a, Optional<String> b, Optional<String> c) {
        sender.sendMessage("a: " + a.orElse("A") + ", b: " + b.orElse("B") + ", c: " + c.orElse("C"));
    }

    @SubCommand("color <?color> <?color> <?color>")
    public void colors(Sender sender, Optional<TextColor> a, Optional<TextColor> b, Optional<TextColor> c) {
        sender.sendMessage("a: " + a.orElse(NamedTextColor.BLACK) + ", b: " + b.orElse(NamedTextColor.GRAY) + ", c: " + c.orElse(NamedTextColor.WHITE));
    }

    @SourceParser(keyword = "color", clazz = TextColor.class, resultMaxAgeInMills = -1, ignoreCase = true)
    public Map<String, TextColor> getColor() {
        return Map.ofEntries(
                Map.entry("black", NamedTextColor.BLACK),
                Map.entry("dark_blue", NamedTextColor.DARK_BLUE),
                Map.entry("dark_green", NamedTextColor.DARK_GREEN),
                Map.entry("dark_aqua", NamedTextColor.DARK_AQUA),
                Map.entry("dark_red", NamedTextColor.DARK_RED),
                Map.entry("dark_purple", NamedTextColor.DARK_PURPLE),
                Map.entry("gold", NamedTextColor.GOLD),
                Map.entry("gray", NamedTextColor.GRAY),
                Map.entry("dark_gray", NamedTextColor.DARK_GRAY),
                Map.entry("blue", NamedTextColor.BLUE),
                Map.entry("green", NamedTextColor.GREEN),
                Map.entry("aqua", NamedTextColor.AQUA),
                Map.entry("red", NamedTextColor.RED),
                Map.entry("light_purple", NamedTextColor.LIGHT_PURPLE),
                Map.entry("yellow", NamedTextColor.YELLOW),
                Map.entry("white", NamedTextColor.WHITE)
        );
    }

}
