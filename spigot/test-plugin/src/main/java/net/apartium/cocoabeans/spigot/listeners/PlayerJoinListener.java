package net.apartium.cocoabeans.spigot.listeners;

import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.spigot.board.BoardManager;
import net.apartium.cocoabeans.spigot.state.DoubleTimeJumpObservable;
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

    private final Observable<Component> heartbeatComponent;

    public PlayerJoinListener(BoardManager boardManager) {
        this.boardManager = boardManager;

        heartbeatComponent = boardManager.getHeartbeatTime()
                .map(bpm -> Component.text(bpm + " bpm", NamedTextColor.RED));
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        CocoaBoard board = boardManager.getBoard(event.getPlayer());

        board.lines(List.of(
                boardManager.getNowFormated().map(s -> Component.text(s).color(NamedTextColor.GRAY)),
                Observable.empty(),
                heartbeatComponent,
                boardManager.getPlayerCount().map(playerCount -> Component.text("Players: ").append(Component.text(playerCount).color(NamedTextColor.GREEN))),
                Observable.empty(),
                new DoubleTimeJumpObservable(boardManager.getMoney().map(i -> i + 0.0), boardManager.getCurrentTick(), Duration.ofMillis(750), Duration.ofMillis(50))
                        .map(money -> Component.text("Money ", NamedTextColor.GRAY).append(Component.text(String.format("%.1f", money), NamedTextColor.RED))),
                Observable.immutable(Component.text("§7example.com"))
        ));
    }
}
