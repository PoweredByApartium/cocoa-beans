package net.apartium.cocoabeans.spigot.board;

import com.sun.management.OperatingSystemMXBean;
import net.apartium.cocoabeans.scoreboard.CocoaBoard;
import net.apartium.cocoabeans.scoreboard.spigot.SpigotCocoaBoard;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
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

    private final Map<UUID, MutableObservable<Integer>> playerWalked = new HashMap<>();

    private final MutableObservable<Instant> nowState = Observable.mutable(Instant.now());
    private final Observable<String> nowFormated = nowState.map((now) -> now.atZone(zoneId).format(TIME_FORMATTER));
    private final MutableObservable<Integer> playerCount;

    private final MutableObservable<Integer> currentTick;
    private final MutableObservable<Long> heartbeatTime;
    private final MutableObservable<Double> tps1m;
    private final MutableObservable<Double> tps5m;
    private final MutableObservable<Double> tps15m;
    private final MutableObservable<Long> mspt;

    private final Observable<Long> usedMemory;
    private final MutableObservable<Long> totalMemory;
    private final MutableObservable<Long> freeMemory;

    private final MutableObservable<Integer> money;

    private final MutableObservable<Double> cpuUsage;
    private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    private BukkitTask cprTask;

    public BoardManager() {
        currentTick = Observable.mutable(Bukkit.getCurrentTick());
        heartbeatTime = Observable.mutable(0L);
        tps1m = Observable.mutable(Bukkit.getTPS()[0]);
        tps5m = Observable.mutable(Bukkit.getTPS()[1]);
        tps15m = Observable.mutable(Bukkit.getTPS()[2]);

        money = Observable.mutable(100);

        mspt = Observable.mutable(0L);

        Runtime runtime = Runtime.getRuntime();

        totalMemory = Observable.mutable(runtime.totalMemory());
        freeMemory = Observable.mutable(runtime.freeMemory());

        usedMemory = Observable.compound(totalMemory, freeMemory)
                .map((t) -> t.arg0() - t.arg1());


        cpuUsage = Observable.mutable(osBean.getCpuLoad());
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
        return boards.computeIfAbsent(player.getUniqueId(), uuid -> SpigotCocoaBoard.create(player));
    }

    public void unregisterBoard(UUID targetUUID) {
        boards.remove(targetUUID);
        playerWalked.remove(targetUUID);
    }

    public void heartbeat() {
        long startTime = System.currentTimeMillis();

        currentTick.set(Bukkit.getCurrentTick());
        Runtime runtime = Runtime.getRuntime();
        totalMemory.set(runtime.totalMemory());
        freeMemory.set(runtime.freeMemory());
        cpuUsage.set(osBean.getCpuLoad());

        double[] tps = Bukkit.getTPS();

        tps1m.set(tps[0]);
        tps5m.set(tps[1]);
        tps15m.set(tps[2]);

        for (Player player : Bukkit.getOnlinePlayers()) {
            MutableObservable<Integer> observable = playerWalked.get(player.getUniqueId());
            if (observable == null)
                continue;

            observable.set(player.getStatistic(Statistic.WALK_ONE_CM) + player.getStatistic(Statistic.SPRINT_ONE_CM));
        }

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

    public MutableObservable<Double> getTps1m() {
        return tps1m;
    }

    public MutableObservable<Double> getTps5m() {
        return tps5m;
    }

    public MutableObservable<Double> getTps15m() {
        return tps15m;
    }

    public MutableObservable<Long> getMspt() {
        return mspt;
    }

    public MutableObservable<Integer> getPlayerCount() {
        return playerCount;
    }

    public Observable<String> getNowFormated() {
        return nowFormated;
    }

    public Observable<Long> getUsedMemory() {
        return usedMemory;
    }

    public MutableObservable<Long> getTotalMemory() {
        return totalMemory;
    }

    public MutableObservable<Long> getFreeMemory() {
        return freeMemory;
    }

    public MutableObservable<Integer> getMoney() {
        return money;
    }

    public MutableObservable<Double> getCpuUsage() {
        return cpuUsage;
    }

    public Map<UUID, MutableObservable<Integer>> getPlayerWalked() {
        return playerWalked;
    }
}
