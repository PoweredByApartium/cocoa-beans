package net.apartium.cocoabeans.spigot.listeners;

import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.spigot.board.BoardManager;
import net.apartium.cocoabeans.spigot.state.DoubleTimeJumpObservable;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.List;

public class PlayerJoinListener implements Listener {
    private final BoardManager boardManager;

    private final Observable<Component> tpsComponent;

    private final Observable<Component> memoryComponent;
    private final Observable<Component> heartbeatComponent;

    private final Observable<Component> memoryBar;

    private final Observable<Component> cpuBar;


    public PlayerJoinListener(BoardManager boardManager) {
        this.boardManager = boardManager;

        tpsComponent = Observable.compound(boardManager.getTps1m(), boardManager.getTps5m(), boardManager.getTps15m())
                .map((tps) -> Component.text("TPS ", NamedTextColor.GOLD)
                        .append(formatTps(tps.arg0()))
                        .append(Component.text(", ", NamedTextColor.WHITE))
                        .append(formatTps(tps.arg1()))
                        .append(Component.text(", ", NamedTextColor.WHITE))
                        .append(formatTps(tps.arg2())));

        memoryComponent = Observable.compound(boardManager.getTotalMemory(), boardManager.getUsedMemory(), boardManager.getFreeMemory())
                .map((mem) -> Component.text("TUF: ")
                        .append(Component.text((mem.arg0() / (1024 * 1024)) + " MB", NamedTextColor.GRAY))
                        .append(Component.text("  "))
                        .append(Component.text((mem.arg1() / (1024 * 1024)) + " MB", NamedTextColor.DARK_GRAY))
                        .append(Component.text(" "))
                        .append(Component.text((mem.arg2() / (1024 * 1024)) + " MB", NamedTextColor.GREEN)));

        heartbeatComponent = boardManager.getHeartbeatTime()
                .map(bpm -> Component.text(bpm + " bpm", NamedTextColor.RED));

        memoryBar = Observable.compound(boardManager.getTotalMemory(), boardManager.getUsedMemory())
                .map((mem) -> {
                    int barLength = 50;
                    int usedBars = (int) ((mem.arg1() / (double) mem.arg0()) * barLength);
                    int freeBars = barLength - usedBars;

                    TextColor startColor = TextColor.color(0, 255, 0);
                    TextColor endColor = TextColor.color(255, 0, 0);

                    Component bar = Component.text("");
                    for (int i = 0; i < usedBars; i++) {
                        double t = (double) i / (barLength - 1);
                        bar = bar.append(Component.text("|", lerp(startColor, endColor, t)));
                    }

                    for (int i = 0; i < freeBars; i++) {
                        bar = bar.append(Component.text("|", NamedTextColor.DARK_GRAY));
                    }

                    return bar;
                });

        cpuBar = boardManager.getCpuUsage()
                .map((cpuUsage) -> {
                    int barLength = 50;
                    int usedBars = (int) (cpuUsage * barLength);
                    int freeBars = barLength - usedBars;

                    TextColor startColor = TextColor.color(0, 255, 0);
                    TextColor endColor = TextColor.color(255, 0, 0);

                    Component bar = Component.text("");
                    for (int i = 0; i < usedBars; i++) {
                        double t = (double) i / (barLength - 1);
                        bar = bar.append(Component.text("|", lerp(startColor, endColor, t)));
                    }

                    for (int i = 0; i < freeBars; i++) {
                        bar = bar.append(Component.text("|", NamedTextColor.DARK_GRAY));
                    }

                    return bar.append(Component.text(" " + String.format("%.1f", cpuUsage * 100) + "%", NamedTextColor.GRAY));
                });
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static TextColor lerp(TextColor a, TextColor b, double t) {
        return TextColor.color(
                (int) lerp(a.red(), b.red(), t),
                (int) lerp(a.green(), b.green(), t),
                (int) lerp(a.blue(), b.blue(), t)
        );
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        MutableObservable<Integer> walk = Observable.mutable(0);
        boardManager.getPlayerWalked().put(player.getUniqueId(), walk);

        CocoaBoard board = boardManager.getBoard(event.getPlayer());

        board.lines(List.of(
                boardManager.getNowFormated().map(s -> Component.text(s).color(NamedTextColor.GRAY)),
                Observable.empty(),
                heartbeatComponent,
                tpsComponent,
                memoryComponent,
                memoryBar,
                cpuBar,
                boardManager.getPlayerCount().map(playerCount -> Component.text("Players: ").append(Component.text(playerCount).color(NamedTextColor.GREEN))),
                Observable.empty(),
                walk.map(cm -> Component.text("Walked: ", NamedTextColor.GRAY).append(Component.text(cm, NamedTextColor.RED))),
                new DoubleTimeJumpObservable(boardManager.getMoney().map(i -> i + 0.0), boardManager.getCurrentTick(), Duration.ofMillis(750), Duration.ofMillis(50))
                        .map(money -> Component.text("Money ", NamedTextColor.GRAY).append(Component.text(String.format("%.1f", money), NamedTextColor.RED))),
                Observable.immutable(Component.text("§7example.com"))
        ));
    }

    private Component formatTps(double tps) {
        TextColor color;
        if (tps >= 19.9) {
            color = NamedTextColor.DARK_GREEN;
        } else if (tps >= 18) {
            color = NamedTextColor.GREEN;
        } else if (tps >= 15) {
            color = NamedTextColor.YELLOW;
        } else if (tps >= 12.5) {
            color = NamedTextColor.GOLD;
        } else if (tps >= 10) {
            color = NamedTextColor.RED;
        } else {
            color = NamedTextColor.DARK_RED;
        }

        return Component.text(String.format("%.1f", tps), color);
    }
}
