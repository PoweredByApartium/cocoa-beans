package net.apartium.cocoabeans.spigot.commands;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.parsers.QuotedStringParser;
import net.apartium.cocoabeans.commands.parsers.SourceParser;
import net.apartium.cocoabeans.commands.parsers.StringParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.spigot.board.BoardManager;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.animation.FadingColorBlinkObservable;
import net.apartium.cocoabeans.state.animation.FadingColorInOutObservable;
import net.apartium.cocoabeans.state.animation.FixedDoubleLerpObservable;
import net.apartium.cocoabeans.state.animation.TypingObservable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@WithParser(value = QuotedStringParser.class)
@WithParser(value = StringParser.class, priority = -1)
@SenderLimit(SenderType.PLAYER)
@Command(value = "cocoaboard", aliases = "cb")
public class CocoaBoardCommand implements CommandNode {

    private final BoardManager boardManager;

    public CocoaBoardCommand(BoardManager boardManager) {
        this.boardManager = boardManager;
    }

    @SubCommand("scoreboard")
    public void scoreboardTest(Player player) {
        player.sendMessage("Doing some test");

        boardManager.getBoard(player);
    }

    @SubCommand("scoreboard set <int> <strings>")
    public void setLine(Player player, int line, String text) {
        CocoaBoard board = boardManager.getBoard(player);

        board.line(line, Component.text(text));
    }

    @SubCommand("scoreboard set <int> <quoted-string> <quoted-string>")
    public void setLine(Player player, int line, String text, String scoreDisplay) {
        CocoaBoard board = boardManager.getBoard(player);

        board.line(line, Component.text(text), Component.text(scoreDisplay));
    }

    @SubCommand("scoreboard set display")
    public void setDisplay(Player player) {
        CocoaBoard board = boardManager.getBoard(player);

        board.setDisplay();
    }

    @SubCommand("scoreboard clear")
    public void clear(Player player) {
        CocoaBoard board = boardManager.getBoard(player);

        board.lines(List.of());
        player.sendMessage("Cleared");
    }

    @SubCommand("scoreboard add-lines")
    public void addLines(Player player) {
        List<Observable<Component>> list = List.of(
                boardManager.getNowFormated().map(s -> Component.text(s).color(NamedTextColor.GRAY)),
                Observable.empty(),
                Observable.immutable(Component.text("Welcome to Test")),
                Observable.immutable(Component.text("Bla bla bla")),
                Observable.empty(),
                boardManager.getCurrentTick().map(tick -> MiniMessage.miniMessage().deserialize("<rainbow:" + tick + ">" + "|".repeat(16) + "</rainbow>")),
                boardManager.getPlayerCount().map(playerCount -> Component.text("Players: ").append(Component.text(playerCount).color(NamedTextColor.GREEN))),
                Observable.empty(),
                Observable.immutable(Component.text("§7example.com"))
        );

        CocoaBoard board = boardManager.getBoard(player);

        board.lines(list);
        player.sendMessage("working");
    }

    @SubCommand("player <player> add-lines")
    public void addLines(Player player, Player target) {
        List<Observable<Component>> list = List.of(
                boardManager.getNowFormated().map(s -> Component.text(s).color(NamedTextColor.GRAY)),
                Observable.empty(),
                Observable.immutable(Component.text("Welcome to Test")),
                Observable.immutable(Component.text("Bla bla bla")),
                Observable.empty(),
                boardManager.getCurrentTick().map(tick -> MiniMessage.miniMessage().deserialize("<rainbow:" + tick + ">" + "|".repeat(16) + "</rainbow>")),
                boardManager.getPlayerCount().map(playerCount -> Component.text("Players: ").append(Component.text(playerCount).color(NamedTextColor.GREEN))),
                Observable.empty(),
                Observable.immutable(Component.text("§7example.com"))
        );

        CocoaBoard board = boardManager.getBoard(target);

        board.lines(list);
        player.sendMessage("working");
    }

    @SubCommand("scoreboard add-lines for")
    public void addLinesFor(Player player) {
        List<Observable<Component>> list = List.of(
                boardManager.getNowFormated().map(s -> Component.text(s).color(NamedTextColor.GRAY)),
                Observable.empty(),
                Observable.immutable(Component.text("Welcome to Test")),
                Observable.immutable(Component.text("Bla bla bla")),
                Observable.empty(),
                boardManager.getCurrentTick().map(tick -> MiniMessage.miniMessage().deserialize("<rainbow:" + tick + ">" + "|".repeat(16) + "</rainbow>")),
                boardManager.getPlayerCount().map(playerCount -> Component.text("Players: ").append(Component.text(playerCount).color(NamedTextColor.GREEN))),
                Observable.empty(),
                Observable.immutable(Component.text("§7example.com"))
        );

        CocoaBoard board = boardManager.getBoard(player);

        for (int i = 0; i < list.size(); i++) {
            board.line(i, list.get(i));
        }

        player.sendMessage("working");
    }

    @SubCommand("scoreboard set lines simple")
    public void setLinesSimple(Player player) {
        CocoaBoard board = boardManager.getBoard(player);

        board.updateLines(List.of(
                Component.text("test"),
                Component.empty(),
                Component.empty(),
                Component.text("Players: 1"),
                Component.empty(),
                Component.text("Balance: 123,210"),
                Component.text("Level: 12"),
                Component.text("Exp: 63"),
                Component.empty(),
                Component.text("example.com", NamedTextColor.GRAY)
        ));
        player.sendMessage("working");
    }

    @SubCommand("scoreboard add <quoted-string>")
    public void addLine(Player player, String text) {
        player.sendMessage("mhm");
        boardManager.getBoard(player).add(Component.text(text));

        player.sendMessage("add line");
    }

    @SubCommand("scoreboard add <quoted-string> <int>")
    public void addLine(Player player, String text, int line) {
        boardManager.getBoard(player).add(Component.text(text), line);

        player.sendMessage("add line");
    }

    @SubCommand("document scoreboard animated name <quoted-string> <?color> <?color> <?color>")
    public void animatedTitle(Player player, String text, Optional<TextColor> in, Optional<TextColor> fade, Optional<TextColor> out) {
        boardManager.getBoard(player).title(new FadingColorBlinkObservable(
                        Observable.immutable(text),
                        boardManager.getNow(),
                        Duration.ofMillis(60 * 50L),
                        Duration.ofMillis(2 * 50L),
                        3,
                        Duration.ofMillis(7 * 50L),
                        Style.style(in.orElse(NamedTextColor.YELLOW), TextDecoration.BOLD),
                        Style.style(fade.orElse(NamedTextColor.GOLD), TextDecoration.BOLD),
                        Style.style(out.orElse(NamedTextColor.WHITE), TextDecoration.BOLD)
                )
        );
    }

    @SubCommand("document scoreboard animated line <quoted-string> <?color> <?color> <?color>")
    public void animatedLine(Player player, String text, Optional<TextColor> in, Optional<TextColor> fade, Optional<TextColor> out) {
        boardManager.getBoard(player).add(new FadingColorBlinkObservable(
                        Observable.immutable(text),
                        boardManager.getNow(),
                        Duration.ofMillis(60 * 50L),
                        Duration.ofMillis(2 * 50L),
                        3,
                        Duration.ofMillis(7 * 50L),
                        Style.style(in.orElse(NamedTextColor.YELLOW), TextDecoration.BOLD),
                        Style.style(fade.orElse(NamedTextColor.GOLD), TextDecoration.BOLD),
                        Style.style(out.orElse(NamedTextColor.WHITE), TextDecoration.BOLD)
                )
        );
    }

    @SubCommand("document scoreboard typing animated <quoted-string> <?color> <?color> <?color>")
    public void animatedTyping(Player player, String text, Optional<TextColor> in, Optional<TextColor> fade, Optional<TextColor> out) {
        boardManager.getBoard(player).add(new FadingColorBlinkObservable(
                        new TypingObservable(
                                Observable.immutable(text),
                                boardManager.getNow(),
                                Duration.ofMillis(1200),
                                Duration.ofSeconds(10),
                                Duration.ofMillis(100),
                                "§k"
                        ),
                        boardManager.getNow(),
                        Duration.ofMillis(60 * 50L),
                        Duration.ofMillis(2 * 50L),
                        3,
                        Duration.ofMillis(7 * 50L),
                        Style.style(in.orElse(NamedTextColor.YELLOW), TextDecoration.BOLD),
                        Style.style(fade.orElse(NamedTextColor.GOLD), TextDecoration.BOLD),
                        Style.style(out.orElse(NamedTextColor.WHITE), TextDecoration.BOLD)
                )
        );
    }

    @SubCommand("document scoreboard typing <quoted-string>")
    public void addTyping(Player player, String text) {
        boardManager.getBoard(player).add(new TypingObservable(
                Observable.immutable(text),
                boardManager.getNow(),
                Duration.ofMillis(50 * 50L),
                Duration.ofMillis(50 * 50L),
                Duration.ofMillis(2 * 50L),
                "§k"
        ).map(Component::text));
    }

    @SubCommand("scoreboard add-money")
    public void addMoney(Player player) {
        boardManager.getBoard(player).add(new FixedDoubleLerpObservable(
                boardManager.getMoney().map(i -> i + 0.0),
                boardManager.getNow(),
                Duration.ofMillis(750)
        ).map(money -> Component.text("Money ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.1f", money), NamedTextColor.RED))
        ));
    }

    @SubCommand("scoreboard onJoin")
    public void onJoin(Player player) {
        onJoin(boardManager.getBoard(player));
    }

    public void onJoin(CocoaBoard board) {
        board.title(new FadingColorInOutObservable(
                Observable.immutable("ProjectZero"),
                boardManager.getNow(),
                Duration.ofSeconds(3),
                Duration.ofMillis(750),
                Duration.ofMillis(2 * 50L),
                Style.style(NamedTextColor.GREEN, TextDecoration.BOLD),
                Style.style(NamedTextColor.DARK_GREEN, TextDecoration.BOLD),
                Style.style(NamedTextColor.DARK_GREEN, TextDecoration.BOLD)
        ));

        board.add(Component.text("Simple scoreboard animations"));

        board.add(new FixedDoubleLerpObservable(
                boardManager.getMoney().map(i -> i + 0.0),
                boardManager.getNow(),
                Duration.ofMillis(750)
        ).map(money -> Component.text("Money ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.format("%.1f", money), NamedTextColor.RED))
        ));

        board.add(new TypingObservable(
                Observable.immutable("Hello, World!"),
                boardManager.getNow(),
                Duration.ofMillis(50 * 50L),
                Duration.ofMillis(50 * 50L),
                Duration.ofMillis(2 * 50L),
                "§k"
        ).map(Component::text));

        board.add(new FadingColorInOutObservable(
                Observable.immutable("example.com"),
                boardManager.getNow(),
                Duration.ofSeconds(3),
                Duration.ofMillis(750),
                Duration.ofMillis(2 * 50L),
                Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD),
                Style.style(NamedTextColor.GOLD, TextDecoration.BOLD),
                Style.style(NamedTextColor.WHITE, TextDecoration.BOLD)
        ));
    }

    @SubCommand("scoreboard add-money <player>")
    public void addMoney(Player player, Player target) {
        boardManager.getBoard(target).add(new FixedDoubleLerpObservable(
                boardManager.getMoney().map(i -> i + 0.0),
                boardManager.getNow(),
                Duration.ofMillis(750)
        ).map(money -> Component.text("Money ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.1f", money), NamedTextColor.RED))
        ));
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

    @SubCommand("scoreboard add <quoted-string> <color>")
    public void addLine(Player player, String text, TextColor color) {
        boardManager.getBoard(player).add(Component.text(text), null, Style.style(color));

        player.sendMessage("add line");
    }

    @SubCommand("scoreboard add <quoted-string> <color> <int>")
    public void addLine(Player player, String text, TextColor color, int line) {
        boardManager.getBoard(player).add(Component.text(text), null, Style.style(color), line);

        player.sendMessage("add line");
    }

    @SubCommand("scoreboard set style <int> <color> <boolean>")
    public void setStyle(Player player, int line, TextColor color, boolean boldOptional) {
        boardManager.getBoard(player).numberStyle(line, Style.style(color, boldOptional ? Set.of(TextDecoration.BOLD) : Set.of()));
    }

    @SubCommand("money set <int>")
    public void setMoney(Player player, int money) {
        boardManager.getMoney().set(money);
        player.sendMessage("money has been set");
    }

    @SubCommand("scoreboard remove <int>")
    public void removeLines(Player player, int line) {
        CocoaBoard board = boardManager.getBoard(player);
        board.remove(line);

        player.sendMessage("Line has been removed");
    }

    @SubCommand("name <string>")
    public void nameTest(Player player, String name) {
        CocoaBoard board = boardManager.getBoard(player);

        board.title(Component.text(ChatColor.translateAlternateColorCodes('&', name)));
    }

    @SubCommand("name animated <string> <?color> <?color> <?color>")
    public void nameTestAnimated(Player player, String name, Optional<TextColor> in, Optional<TextColor> fade, Optional<TextColor> out) {
        CocoaBoard board = boardManager.getBoard(player);

        board.title(new FadingColorInOutObservable(
                Observable.immutable(name),
                boardManager.getNow(),
                Duration.ofSeconds(3),
                Duration.ofMillis(750),
                Duration.ofMillis(2 * 50L),
                Style.style(in.orElse(NamedTextColor.YELLOW), TextDecoration.BOLD),
                Style.style(fade.orElse(NamedTextColor.GOLD), TextDecoration.BOLD),
                Style.style(out.orElse(NamedTextColor.WHITE), TextDecoration.BOLD)
        ));
    }

    @SubCommand("player <player> name animated <string> <?color> <?color> <?color>")
    public void nameForOtherTestAnimated(Player player, Player target, String name, Optional<TextColor> in, Optional<TextColor> fade, Optional<TextColor> out) {
        CocoaBoard board = boardManager.getBoard(target);

        board.title(new FadingColorInOutObservable(
                Observable.immutable(name),
                boardManager.getNow(),
                Duration.ofSeconds(3),
                Duration.ofMillis(750),
                Duration.ofMillis(2 * 50L),
                Style.style(in.orElse(NamedTextColor.YELLOW), TextDecoration.BOLD),
                Style.style(fade.orElse(NamedTextColor.GOLD), TextDecoration.BOLD),
                Style.style(out.orElse(NamedTextColor.WHITE), TextDecoration.BOLD)
        ));
    }

    @SubCommand("get version")
    public void getVersion(CommandSender sender) {
        sender.sendMessage("Version: " + ServerUtils.getVersion());
        sender.sendMessage("McVersion: " + ServerUtils.getVersion().major() + "." + ServerUtils.getVersion().update() + "." + ServerUtils.getVersion().minor());
        sender.sendMessage("Protocol: " + ServerUtils.getVersion().protocol());
    }

    @Override
    public boolean fallbackHandle(Sender sender, String label, String[] args) {
        sender.sendMessage("§cInvalid usage for /" + label);
        return true;
    }
}
