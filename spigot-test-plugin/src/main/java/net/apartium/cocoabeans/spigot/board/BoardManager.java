package net.apartium.cocoabeans.spigot.board;

import com.sun.management.OperatingSystemMXBean;
import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.spigot.scoreboard.SpigotCocoaBoard;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.spigot.SpigotProvidedState;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BoardManager {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.systemDefault();

    private final OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final Runtime runtime = Runtime.getRuntime();

    private final Map<UUID, CocoaBoard> boards = new HashMap<>();
    private final Observable<String> nowFormated;
    private final Observable<Integer> playerCount;

    private final Observable<Integer> currentTick;
    private final Observable<Instant> nowObservable;
    private final MutableObservable<Long> heartbeatTime;

    private final MutableObservable<Integer> money;

    private final MutableObservable<Long> totalMemory;
    private final Observable<Long> usedMemory;
    private final MutableObservable<Long> freeMemory;

    private final MutableObservable<Double> cpuUsage;

    private BukkitTask cprTask;

    public BoardManager(SpigotProvidedState spigotProvidedState) {
        currentTick = spigotProvidedState.currentTickObservable();
        nowObservable = spigotProvidedState.getNow();
        nowFormated = nowObservable.map(now -> now.atZone(zoneId).format(TIME_FORMATTER));
        heartbeatTime = Observable.mutable(0L);
        money = Observable.mutable(100);
        playerCount = spigotProvidedState.getOnlinePlayersObservable().map(Collection::size);

        totalMemory = Observable.mutable(0L);
        freeMemory = Observable.mutable(0L);
        usedMemory = Observable.compound(totalMemory, freeMemory)
                .map((args) -> args.arg0() - args.arg1());

        cpuUsage = Observable.mutable(0.0);
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

    private void updateMemory() {
        totalMemory.set(runtime.totalMemory());
        freeMemory.set(runtime.freeMemory());
    }

    private void updateCpuUsage() {
        cpuUsage.set(Math.max(0.0, Math.min(1.0, operatingSystemMXBean.getCpuLoad())));
    }

    public void heartbeat() {
        long startTime = System.currentTimeMillis();

        updateMemory();
        updateCpuUsage();

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

    public Observable<Long> getTotalMemory() {
        return totalMemory;
    }

    public Observable<Long> getFreeMemory() {
        return freeMemory;
    }

    public Observable<Long> getUsedMemory() {
        return usedMemory;
    }

    public Observable<Double> getCpuUsage() {
        return cpuUsage;
    }
}
