package net.apartium.cocoabeans.spigot.board;

import com.sun.management.OperatingSystemMXBean;
import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.scoreboard.spigot.SpigotCocoaBoard;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoardManager {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.systemDefault();

    private final Map<UUID, CocoaBoard> boards = new HashMap<>();
    private final MutableObservable<Instant> nowState = Observable.mutable(Instant.now());
    private final Observable<String> nowFormated = nowState.map((now) -> now.atZone(zoneId).format(TIME_FORMATTER));
    private final MutableObservable<Integer> playerCount;

    private final MutableObservable<Integer> currentTick;
    private final MutableObservable<Long> heartbeatTime;

    private final MutableObservable<Integer> money;
    private BukkitTask cprTask;

    public BoardManager() {
        currentTick = Observable.mutable(0);
        heartbeatTime = Observable.mutable(0L);
        money = Observable.mutable(100);
        playerCount = Observable.mutable(Bukkit.getOnlinePlayers().size());
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
        boards.remove(targetUUID);
    }

    public void heartbeat() {
        long startTime = System.currentTimeMillis();

        currentTick.set(currentTick.get() + 1);
        boards.values().forEach(CocoaBoard::heartbeat);
        long endTime = System.currentTimeMillis();
        heartbeatTime.set(endTime - startTime);

        nowState.set(Instant.now());
        playerCount.set(Bukkit.getOnlinePlayers().size());

        for (CocoaBoard board : boards.values())
            board.heartbeat();
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

    public MutableObservable<Integer> getCurrentTick() {
        return currentTick;
    }

    public MutableObservable<Long> getHeartbeatTime() {
        return heartbeatTime;
    }

    public MutableObservable<Integer> getPlayerCount() {
        return playerCount;
    }

    public Observable<String> getNowFormated() {
        return nowFormated;
    }

    public MutableObservable<Integer> getMoney() {
        return money;
    }

}
