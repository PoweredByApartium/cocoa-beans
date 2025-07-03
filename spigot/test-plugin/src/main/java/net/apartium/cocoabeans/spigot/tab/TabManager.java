package net.apartium.cocoabeans.spigot.tab;

import net.apartium.cocoabeans.scoreboard.team.DisplayTeam;
import net.apartium.cocoabeans.spigot.scoreboard.SpigotDisplayTeam;
import net.apartium.cocoabeans.spigot.scoreboard.SpigotViewerGroup;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.tab.TabList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class TabManager {

    private final Map<String, SpigotTabList> tabs = new HashMap<>();
    private final MutableObservable<Instant> nowState = Observable.mutable(Instant.now());

    private BukkitTask cprTask;

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

    public void heartbeat() {
        nowState.set(Instant.now());

        for (SpigotTabList tabList : tabs.values())
            tabList.heartbeat();
    }

    public void disable() {
        if (cprTask != null)
            cprTask.cancel();

        cprTask = null;

        for (SpigotTabList tabList : tabs.values())
            tabList.delete();

        tabs.clear();
    }

    public TabList<Player> getTabList(String name) {
        return tabs.computeIfAbsent(name, key -> new SpigotTabList(new SpigotViewerGroup(Collections.newSetFromMap(new WeakHashMap<>()))));
    }

    public boolean removeTabList(String name) {
        SpigotTabList tab = tabs.remove(name);
        if (tab == null)
            return false;

        tab.delete();
        return true;
    }

    public Map<String, TabList<Player>> getTabs() {
        return Collections.unmodifiableMap(tabs);
    }

    public Observable<Instant> getNow() {
        return nowState;
    }
}
