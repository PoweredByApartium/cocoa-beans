package net.apartium.cocoabeans.spigot.commands;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.exception.InvalidUsageResponse;
import net.apartium.cocoabeans.commands.parsers.QuotedStringParser;
import net.apartium.cocoabeans.commands.parsers.SourceParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import net.apartium.cocoabeans.spigot.tab.TabManager;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.animation.FadingColorInOutObservable;
import net.apartium.cocoabeans.tab.TabList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@WithParser(value = QuotedStringParser.class)
@Command("tablist")
public class TabListCommand implements CommandNode {

    private final TabManager tabManager;

    @SourceParser(keyword = "tab", clazz = TabList.class)
    public Map<String, TabList<Player>> getTabList() {
        return tabManager.getTabs();
    }

    @SourceParser(keyword = "tab-keys", clazz = String.class)
    public Map<String, String> getTabKeys() {
        return tabManager.getTabs()
                .keySet()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        Function.identity()
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

    public TabListCommand(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    @SubCommand("create <string>")
    public void createTab(CommandSender sender, String tabId) {
        tabManager.getTabList(tabId);

        sender.sendMessage("Created " + tabId);
    }

    @SubCommand("delete <tab-keys>")
    public void deleteTab(CommandSender sender, String tabId) {
        tabManager.removeTabList(tabId);

        sender.sendMessage("Deleting: " + tabId);
    }

    @SubCommand("viewers <tab> list")
    public void viewers(CommandSender sender, TabList<Player> tabList) {
        sender.sendMessage(Component.text("Viewers: ").append(Component.join(
                JoinConfiguration.builder()
                        .separator(Component.text(", "))
                        .build(),
                tabList.getGroup().players().stream().map(Player::name).toList()
        )));
    }

    @SubCommand("viewers <tab> add <player>")
    public void addViewerToTab(CommandSender sender, TabList<Player> tabList, Player target) {
        tabList.getGroup().add(target);
        sender.sendMessage("Added " + target.getName());
    }

    @SubCommand("viewers <tab> remove <player>")
    public void removeViewerToTab(CommandSender sender, TabList<Player> tabList, Player target) {
        tabList.getGroup().remove(target);
        sender.sendMessage("Removed " + target.getName());
    }

    @SubCommand("modify <tab> header <quoted-string>")
    public void header(CommandSender sender, TabList<Player> tabList, String text) {
        tabList.header(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
        sender.sendMessage("Setting header!");
    }

    @SubCommand("modify <tab> footer <quoted-string>")
    public void footer(CommandSender sender, TabList<Player> tabList, String text) {
        tabList.footer(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
        sender.sendMessage("Setting footer!");
    }

    @SubCommand("modify <tab> header minimessage static <quoted-string>")
    public void headerMiniMessage(CommandSender sender, TabList<Player> tabList, String text) {
        tabList.header(MiniMessage.miniMessage().deserialize(text));
        sender.sendMessage("Setting header!");
    }

    @SubCommand("modify <tab> footer minimessage static <quoted-string>")
    public void footerMiniMessage(CommandSender sender, TabList<Player> tabList, String text) {
        tabList.footer(MiniMessage.miniMessage().deserialize(text));
        sender.sendMessage("Setting footer!");
    }

    @SubCommand("modify <tab> header fade <quoted-string> <?color> <?color> <?color>")
    public void headerFade(CommandSender sender, TabList<Player> tabList, String text, Optional<TextColor> in, Optional<TextColor> fade, Optional<TextColor> out) {
        tabList.header(new FadingColorInOutObservable(
                Observable.immutable(text),
                tabManager.getNow(),
                Duration.ofMillis(5_000),
                Duration.ofMillis(5_000),
                Duration.ofMillis(50),
                Style.style(in.orElse(NamedTextColor.WHITE)),
                Style.style(fade.orElse(NamedTextColor.GOLD)),
                Style.style(out.orElse(NamedTextColor.YELLOW))
        ));
        sender.sendMessage("Setting header!");
    }

    @SubCommand("modify <tab> footer fade <quoted-string> <?color> <?color> <?color>")
    public void footerFade(CommandSender sender, TabList<Player> tabList, String text, Optional<TextColor> in, Optional<TextColor> fade, Optional<TextColor> out) {
        tabList.footer(new FadingColorInOutObservable(
                Observable.immutable(text),
                tabManager.getNow(),
                Duration.ofMillis(5_000),
                Duration.ofMillis(5_000),
                Duration.ofMillis(50),
                Style.style(in.orElse(NamedTextColor.WHITE)),
                Style.style(fade.orElse(NamedTextColor.GOLD)),
                Style.style(out.orElse(NamedTextColor.YELLOW))
        ));
        sender.sendMessage("Setting footer!");
    }

    @ExceptionHandle(NoSuchElementInMapResponse.NoSuchElementInMapException.class)
    public void noSuchElement(CommandSender sender, NoSuchElementInMapResponse.NoSuchElementInMapException exception) {
        sender.sendMessage("§cDid not find element: " + exception.getAttempted());
    }

    @ExceptionHandle(InvalidUsageResponse.InvalidUsageException.class)
    public void invalidUsage(CommandSender sender) {
        sender.sendMessage("§cUsage: /tablist help");
    }

}
