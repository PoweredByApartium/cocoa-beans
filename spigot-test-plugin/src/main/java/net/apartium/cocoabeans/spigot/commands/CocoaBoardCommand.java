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
import net.apartium.cocoabeans.scoreboard.DisplaySlot;
import net.apartium.cocoabeans.scoreboard.ObjectiveRenderType;
import net.apartium.cocoabeans.spigot.scoreboard.SpigotDisplayTeam;
import net.apartium.cocoabeans.spigot.scoreboard.SpigotScoreboardNumericDisplay;
import net.apartium.cocoabeans.scoreboard.team.ChatFormatting;
import net.apartium.cocoabeans.scoreboard.team.DisplayTeam;
import net.apartium.cocoabeans.scoreboard.team.CollisionRule;
import net.apartium.cocoabeans.scoreboard.team.NameTagVisibilityRule;
import net.apartium.cocoabeans.spigot.ServerUtils;
import net.apartium.cocoabeans.spigot.board.BoardManager;
import net.apartium.cocoabeans.spigot.board.ScoreboardNumericManager;
import net.apartium.cocoabeans.spigot.team.TeamManager;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.animation.FadingColorBlinkObservable;
import net.apartium.cocoabeans.state.animation.FadingColorInOutObservable;
import net.apartium.cocoabeans.state.animation.FixedDoubleLerpObservable;
import net.apartium.cocoabeans.state.animation.TypingObservable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@WithParser(value = QuotedStringParser.class)
@WithParser(value = StringParser.class, priority = -1)
@SenderLimit(SenderType.PLAYER)
@Command(value = "cocoaboard", aliases = "cb")
public class CocoaBoardCommand implements CommandNode {

    private final BoardManager boardManager;
    private final TeamManager teamManager;
    private final ScoreboardNumericManager scoreboardNumericManager;

    public CocoaBoardCommand(BoardManager boardManager, TeamManager teamManager, ScoreboardNumericManager scoreboardNumericManager) {
        this.boardManager = boardManager;
        this.teamManager = teamManager;
        this.scoreboardNumericManager = scoreboardNumericManager;
    }

    @SubCommand("scoreboard")
    public void scoreboardTest(Player player) {
        player.sendMessage("Doing some test");

        boardManager.getBoard(player);
    }

    @SubCommand("numeric set <string> all hp")
    public void setNumber(Player player, String id) {
        SpigotScoreboardNumericDisplay display = scoreboardNumericManager.getDisplay(id);
        for (Player target : Bukkit.getOnlinePlayers()) {
            display.set(
                    target.getName(),
                    scoreboardNumericManager.getHp(target.getName()),
                    null,
                    null
            );
        }
        player.sendMessage("setHP");
    }

    @SubCommand("numeric display <string> add <display-slot>")
    public void addDisplaySlot(Player player, String id, DisplaySlot displaySlot) {
        scoreboardNumericManager.getDisplay(id).addDisplaySlot(displaySlot);
        player.sendMessage("addDisplaySlot");
    }

    @SubCommand("numeric display <string> remove <display-slot>")
    public void removeDisplaySlot(Player player, String id, DisplaySlot displaySlot) {
        scoreboardNumericManager.getDisplay(id).removeDisplaySlot(displaySlot);
        player.sendMessage("removeDisplaySlot");
    }

    @SubCommand("numeric cool <string>")
    public void setCool(Player player, String id) {
        scoreboardNumericManager.getDisplay(id).set(
                player.getName(),
                Observable.immutable(10),
                Observable.immutable(
                        Component.text()
                                .append(player.name())
                                .append(Component.text(" <-> "))
                                .append(Component.score(player.getName(), id))
                                .build()
                ),
                null
        );
    }

    @SubCommand("numeric cool2 <string>")
    public void setCool2(Player player, String id) {
        scoreboardNumericManager.getDisplay(id).set(
                player.getName(),
                Observable.immutable(6),
                Observable.immutable(
                        Component.text()
                                .append(player.name())
                                .append(Component.text(" <-> "))
                                .build()
                ),
                null
        );
    }

    @SubCommand("numeric displayName <string> <quoted-string>")
    public void removeDisplaySlot(Player player, String id, String text) {
        scoreboardNumericManager.getDisplay(id).displayName(Observable.immutable(Component.text(ChatColor.translateAlternateColorCodes('&', text))));
        player.sendMessage("new name");
    }

    @SubCommand("numeric audience <string> add <player>")
    public void addAudience(Player player, String id, Player target) {
        scoreboardNumericManager.getDisplay(id).getViewers().add(target);
        player.sendMessage("addAudience");
    }

    @SubCommand("numeric audience <string> remove <player>")
    public void removeAudience(Player player, String id, Player target) {
        scoreboardNumericManager.getDisplay(id).getViewers().remove(target);
        player.sendMessage("removeAudience");
    }

    @SubCommand("numeric renderType <string> <render-type>")
    public void setRenderType(Player player, String id, ObjectiveRenderType renderType) {
        scoreboardNumericManager.getDisplay(id).renderType(renderType);
        player.sendMessage("setRenderType");
    }

    @SubCommand("team myInit <string>")
    public void create(Player player, String name) {
        try {
            DisplayTeam team = teamManager.getTeam(name)
                    .setPrefix(Observable.compound(
                            new FadingColorInOutObservable(
                                    Observable.immutable("Project"),
                                    boardManager.getNowObservable(),
                                    Duration.ofSeconds(5),
                                    Duration.ofSeconds(5),
                                    Duration.ofMillis(4 * 10),
                                    Style.style(NamedTextColor.DARK_GREEN, TextDecoration.BOLD),
                                    Style.style(NamedTextColor.GREEN, TextDecoration.BOLD),
                                    Style.style(NamedTextColor.GREEN, TextDecoration.BOLD)
                            ),
                            new FadingColorInOutObservable(
                                    Observable.immutable("Zero"),
                                    boardManager.getNowObservable(),
                                    Duration.ofSeconds(5),
                                    Duration.ofSeconds(5),
                                    Duration.ofMillis(7 * 10),
                                    Style.style(NamedTextColor.GREEN, TextDecoration.BOLD),
                                    Style.style(NamedTextColor.DARK_GREEN, TextDecoration.BOLD),
                                    Style.style(NamedTextColor.DARK_GREEN, TextDecoration.BOLD)
                            )
                    ).map(args -> Component.text()
                            .append(args.arg0())
                            .append(args.arg1())
                            .append(Component.text(" "))
                            .build()
                    ))
                    .setFormatting(Observable.immutable(ChatFormatting.RESET));

            player.sendMessage("Nice");
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("Error: " + e.getMessage());
        }
    }

    @SourceParser(keyword = "team", clazz = SpigotDisplayTeam.class)
    public Map<String, SpigotDisplayTeam> getCocoaTeams() {
        return teamManager.getTeams();
    }

    @SourceParser(keyword = "collision-rule", clazz = CollisionRule.class, lax = true, ignoreCase = true, resultMaxAgeInMills = -1)
    public Map<String, CollisionRule> getCollisionRule() {
        return Arrays.stream(CollisionRule.values())
                .collect(Collectors.toMap(
                        collisionRule -> collisionRule.name().toLowerCase(),
                        Function.identity()
                ));
    }

    @SourceParser(keyword = "name-tag-visibility-rule", clazz = NameTagVisibilityRule.class, lax = true, ignoreCase = true, resultMaxAgeInMills = -1)
    public Map<String, NameTagVisibilityRule> getNameTagVisibilityRule() {
        return Arrays.stream(NameTagVisibilityRule.values())
                .collect(Collectors.toMap(
                        nameTagVisibilityRule -> nameTagVisibilityRule.name().toLowerCase(),
                        Function.identity()
                ));
    }

    @SourceParser(keyword = "chat-formatting", clazz = ChatFormatting.class, lax = true, ignoreCase = true, resultMaxAgeInMills = -1)
    public Map<String, ChatFormatting> getChatFormatting() {
        return Arrays.stream(ChatFormatting.values())
                .collect(Collectors.toMap(
                        formatting -> formatting.name().toLowerCase(),
                        Function.identity()
                ));
    }

    @SourceParser(keyword = "display-slot", clazz = DisplaySlot.class, lax = true, ignoreCase = true, resultMaxAgeInMills = -1)
    public Map<String, DisplaySlot> getDisplaySlot() {
        return Arrays.stream(DisplaySlot.values())
                .collect(Collectors.toMap(
                        slot -> slot.name().toLowerCase(),
                        Function.identity()
                ));
    }

    @SourceParser(keyword = "render-type", clazz = ObjectiveRenderType.class, lax = true, ignoreCase = true, resultMaxAgeInMills = -1)
    public Map<String, ObjectiveRenderType> getObjectiveRenderType() {
        return Arrays.stream(ObjectiveRenderType.values())
                .collect(Collectors.toMap(
                        type -> type.name().toLowerCase(),
                        Function.identity()
                ));
    }

    @SubCommand("team modify <team> formatting <chat-formatting>")
    public void setChatFormatting(Player player, SpigotDisplayTeam team, ChatFormatting formatting) {
        team.setFormatting(Observable.immutable(formatting));
        player.sendMessage("§6Set formatting to §c" + formatting.name().toLowerCase());
    }

    @SubCommand("team modify <team> displayName <quoted-string>")
    public void setDisplayName(Player player, SpigotDisplayTeam team, String displayName) {
        team.setDisplayName(Observable.immutable(Component.text(ChatColor.translateAlternateColorCodes('&', displayName))));
        player.sendMessage("§aSet displayName");
    }

    @SubCommand("team modify <team> collision <collision-rule>")
    public void setCollisionRule(Player player, SpigotDisplayTeam team, CollisionRule collisionRule) {
        team.setCollisionRule(Observable.immutable(collisionRule));
        player.sendMessage("§6Set collision to §c" + collisionRule.getSerializedName());
    }

    @SubCommand("team modify <team> nametag <name-tag-visibility-rule>")
    public void setNameTagRule(Player player, SpigotDisplayTeam team, NameTagVisibilityRule nameTagVisibilityRule) {
        team.setNameTagVisibilityRule(Observable.immutable(nameTagVisibilityRule));
        player.sendMessage("§6Set name tag to §c" + nameTagVisibilityRule.getSerializedName());
    }

    @SubCommand("team modify <team> prefix <quoted-string>")
    public void setPrefix(Player player, SpigotDisplayTeam team, String prefix) {
        team.setPrefix(Observable.mutable(Component.text(ChatColor.translateAlternateColorCodes('&', prefix))));
    }

    @SubCommand("team modify <team> suffix <quoted-string>")
    public void setSuffix(Player player, SpigotDisplayTeam team, String suffix) {
        team.setSuffix(Observable.mutable(Component.text(ChatColor.translateAlternateColorCodes('&', suffix))));
    }

    @SubCommand("team modify <team> join <player>")
    public void teamJoin(Player player, SpigotDisplayTeam team, Player target) {
        team.addEntity(target.getName());
    }

    @SubCommand("team modify <team> leave <player>")
    public void teamLeave(Player player, SpigotDisplayTeam team, Player target) {
        team.removeEntity(target.getName());
    }

    @SubCommand("team modify <team> audience add <player>")
    public void addAudience(Player player, SpigotDisplayTeam team, Player target) {
        team.getViewers().add(target);
        player.sendMessage("Joined");
    }

    @SubCommand("team modify <team> audience remove <player>")
    public void removeAudience(Player player, SpigotDisplayTeam team, Player target) {
        team.getViewers().remove(target);
        player.sendMessage("Leaved");
    }

    @SubCommand("team modify <team> friendlyFire <int>")
    public void teamFriendlyFire(Player player, SpigotDisplayTeam team, int friendlyFire) {
        team.setFriendlyFire(Observable.immutable((byte) friendlyFire));
        player.sendMessage("Friendly fire");
    }



    @SubCommand("team heartbeat <team>")
    public void heartbeat(Player player, SpigotDisplayTeam team) {
        team.heartbeat();
        player.sendMessage("Heartbeat");
    }

    @SubCommand("scoreboard delete")
    public void deleteScoreboard(Player player) {
        boardManager.unregisterBoard(player.getUniqueId());
    }

    @SubCommand("scoreboard sidebar")
    public void sidebar(Player player) {
        boardManager.getBoard(player).setDisplay();
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

    @SubCommand("scoreboard add-debug <?int>")
    public void addDebug(Player player, OptionalInt optionalLine) {
        Observable<Component> tufObservable = Observable.compound(
                boardManager.getTotalMemory(),
                boardManager.getUsedMemory(),
                boardManager.getFreeMemory()
        ).map((args) -> {
            long total = args.arg0();
            long used = args.arg1();
            long free = args.arg2();

            return Component.text()
                    .append(Component.text("TUF: ", NamedTextColor.WHITE))
                    .append(Component.text(toHumanByteSize(total), NamedTextColor.GRAY))
                    .append(Component.text(" ", NamedTextColor.WHITE))
                    .append(Component.text(toHumanByteSize(used), NamedTextColor.GRAY))
                    .append(Component.text(" ", NamedTextColor.WHITE))
                    .append(Component.text(toHumanByteSize(free), NamedTextColor.GREEN))
                    .build();
        });

        Observable<Component> barObservable = Observable.compound(
                boardManager.getTotalMemory(),
                boardManager.getUsedMemory()
                ).map((args) -> {
            long total = args.arg0();
            long used = args.arg1();

            return usageBar(((double) used) / (double) total);
        });

        Observable<Component> cpuUsage = boardManager.getCpuUsage().map(this::usageBar);

        if (optionalLine.isEmpty()) {
            boardManager.getBoard(player).add(tufObservable);
            boardManager.getBoard(player).add(barObservable);
            boardManager.getBoard(player).add(Component.text("CPU usage: ", Style.style(NamedTextColor.GRAY, TextDecoration.BOLD)));
            boardManager.getBoard(player).add(cpuUsage);
            return;
        }

        boardManager.getBoard(player).add(tufObservable, optionalLine.getAsInt());
        boardManager.getBoard(player).add(barObservable, optionalLine.getAsInt() + 1);
        boardManager.getBoard(player).add(Component.text("CPU usage: ", Style.style(NamedTextColor.GRAY, TextDecoration.BOLD)),  optionalLine.getAsInt() + 2);
        boardManager.getBoard(player).add(cpuUsage,  optionalLine.getAsInt() + 3);

    }

    private Component usageBar(double percent) {
        final int barLength = 54;
        final TextColor start = TextColor.color(0x1C6900);
        final TextColor middle = TextColor.color(0x8EF58A);
        final TextColor end = TextColor.color(0xFF3217);

        int usedBar = (int) Math.round(percent * barLength);
        int freeBars = barLength - usedBar;

        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < usedBar; i++) {
            float t = (float) i / Math.max(1.0f, barLength - 1.0f);
            builder.append(Component.text(
                    "|",
                    TextColor.lerp(
                            t,
                            start,
                            TextColor.lerp(
                                    t,
                                    middle,
                                    end
                            )
                    )
            ));
        }

        for (int i = 0; i < freeBars; i++)
            builder.append(Component.text("|", NamedTextColor.DARK_GRAY));

        builder.append(Component.text(String.format(
                " (%.1f%%)",
                percent * 100
        )));

        return builder.build();
    }

    private String toHumanByteSize(long size) {
        if (size < 1024)
            return size + "B";

        final String[] units = new String[]{"KB", "MB", "GB", "TB", "PB", "EB"};
        double value = size / 1024.0;
        int i = 0;
        while (value >= 1024 && i < units.length - 1) {
            value /= 1024.0;
            i++;
        }

        return String.format("%.1f " + units[i], value);
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
                        boardManager.getNowObservable(),
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
                        boardManager.getNowObservable(),
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
                                boardManager.getNowObservable(),
                                Duration.ofMillis(1200),
                                Duration.ofSeconds(10),
                                Duration.ofMillis(100),
                                "§k"
                        ),
                        boardManager.getNowObservable(),
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
                boardManager.getNowObservable(),
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
                boardManager.getNowObservable(),
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
                boardManager.getNowObservable(),
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
                boardManager.getNowObservable(),
                Duration.ofMillis(750)
        ).map(money -> Component.text("Money ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.format("%.1f", money), NamedTextColor.RED))
        ));

        board.add(new TypingObservable(
                Observable.immutable("Hello, World!"),
                boardManager.getNowObservable(),
                Duration.ofMillis(50 * 50L),
                Duration.ofMillis(50 * 50L),
                Duration.ofMillis(2 * 50L),
                "§k"
        ).map(Component::text));

        board.add(new FadingColorInOutObservable(
                Observable.immutable("example.com"),
                boardManager.getNowObservable(),
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
                boardManager.getNowObservable(),
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
                boardManager.getNowObservable(),
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
                boardManager.getNowObservable(),
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
