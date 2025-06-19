package net.apartium.cocoabeans.spigot.board;

import net.apartium.cocoabeans.scoreboard.spigot.SpigotBoardPlayerGroup;
import net.apartium.cocoabeans.scoreboard.spigot.SpigotScoreboardNumericDisplay;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.spigot.SpigotProvidedState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ScoreboardNumericManager {

    private final Map<String, SpigotScoreboardNumericDisplay> displays = new HashMap<>();
    private final Observable<Instant> nowObservable;
    private final Map<String, MutableObservable<Integer>> hp = new HashMap<>();
    private BukkitTask cprTask;

    public ScoreboardNumericManager(SpigotProvidedState spigotProvidedState) {
        this.nowObservable = spigotProvidedState.getNow();
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

    public SpigotScoreboardNumericDisplay getDisplay(String id) {
        return displays.computeIfAbsent(
                id,
                key -> new SpigotScoreboardNumericDisplay(
                        key,
                        new SpigotBoardPlayerGroup(Collections.newSetFromMap(new WeakHashMap<>())),
                        Observable.empty()
                )
        );
    }

    public void heartbeat() {
        for (Player player : Bukkit.getOnlinePlayers())
            hp.computeIfAbsent(player.getName(), name -> Observable.mutable(0)).set((int) player.getHealth());

        for (SpigotScoreboardNumericDisplay display : displays.values())
            display.heartbeat();
    }

    public Observable<Integer> getHp(String id) {
        return hp.computeIfAbsent(id, name -> Observable.mutable(0));
    }

}
