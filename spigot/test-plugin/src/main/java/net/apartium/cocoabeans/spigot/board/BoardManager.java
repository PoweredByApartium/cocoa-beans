package net.apartium.cocoabeans.spigot.board;

import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.scoreboard.spigot.SpigotCocoaBoard;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.spigot.SpigotProvidedState;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BoardManager {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.systemDefault();

    private final Map<UUID, CocoaBoard> boards = new HashMap<>();
    private final Observable<String> nowFormated;
    private final Observable<Integer> playerCount;

    private final Observable<Integer> currentTick;
    private final Observable<Instant> nowObservable;
    private final MutableObservable<Long> heartbeatTime;

    private final MutableObservable<Integer> money;
    private BukkitTask cprTask;

    public BoardManager(SpigotProvidedState spigotProvidedState) {
        currentTick = spigotProvidedState.currentTickObservable();
        nowObservable = spigotProvidedState.getNow();
        nowFormated = nowObservable.map(now -> now.atZone(zoneId).format(TIME_FORMATTER));
        heartbeatTime = Observable.mutable(0L);
        money = Observable.mutable(100);
        playerCount = spigotProvidedState.getOnlinePlayersObservable().map(Collection::size);
    }

    public void initialize(JavaPlugin plugin) {
        if (cprTask != null)
            return;

        cprTask = new BukkitRunnable() {
            @Override
            public void run() {
                heartbeat();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public CocoaBoard getBoard(Player player) {
        return boards.computeIfAbsent(player.getUniqueId(), uuid -> SpigotCocoaBoard.create(player, "meow", Component.text("Test")));
    }

    public void unregisterBoard(UUID targetUUID) {
        CocoaBoard board = boards.remove(targetUUID);
        if (board != null)
            board.delete();
    }

    public void heartbeat() {
        long startTime = System.currentTimeMillis();

        for (CocoaBoard board : boards.values())
            board.heartbeat();

        long endTime = System.currentTimeMillis();
        heartbeatTime.set(endTime - startTime);
    }

    public void disable() {
        if (cprTask == null)
            return;

        cprTask.cancel();
        cprTask = null;

        for (CocoaBoard board : boards.values()) {
            board.delete();
        }

        boards.clear();
    }

    // Getters

    public Observable<Integer> getPlayerCount() {
        return playerCount;
    }

    public Observable<Integer> getCurrentTick() {
        return currentTick;
    }

    public MutableObservable<Long> getHeartbeatTime() {
        return heartbeatTime;
    }

    public Observable<String> getNowFormated() {
        return nowFormated;
    }

    public Observable<Instant> getNowObservable() {
        return nowObservable;
    }

    public MutableObservable<Integer> getMoney() {
        return money;
    }

}
