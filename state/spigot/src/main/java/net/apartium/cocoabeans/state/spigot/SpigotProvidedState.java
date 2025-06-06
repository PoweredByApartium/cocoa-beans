package net.apartium.cocoabeans.state.spigot;

import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.SetObservable;
import net.apartium.cocoabeans.state.spigot.listeners.PlayerJoinListener;
import net.apartium.cocoabeans.state.spigot.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *
 */
public class SpigotProvidedState {

    private final JavaPlugin plugin;
    private BukkitTask cprTask = null;
    private PlayerJoinListener playerJoinListener;
    private PlayerQuitListener playerQuitListener;


    private SetObservable<Player> onlinePlayers;
    private MutableObservable<Integer> currentTick;
    private MutableObservable<Instant> now;

    public SpigotProvidedState(JavaPlugin plugin) {
        this.plugin = plugin;

        this.onlinePlayers = Observable.set(new HashSet<>(getOnlinePlayers()));
        this.currentTick = Observable.mutable(0);
        this.now = Observable.mutable(Instant.now());

        playerQuitListener = new PlayerQuitListener(onlinePlayers);
        playerJoinListener = new PlayerJoinListener(onlinePlayers);

        Bukkit.getPluginManager().registerEvents(playerQuitListener, plugin);
        Bukkit.getPluginManager().registerEvents(playerJoinListener, plugin);
    }

    public void heartbeat() {
        this.currentTick.set(this.currentTick.get() + 1);
        this.now.set(Instant.now());
    }

    public void startCprTask() {
        if (cprTask != null) {
            cprTask.cancel();
            cprTask = null;
        }

        cprTask = new BukkitRunnable() {

            @Override
            public void run() {
                heartbeat();
            }

        }.runTaskTimer(plugin, 1, 1);
    }



    private Set<Player> getOnlinePlayers() {
        Set<Player> players = Collections.newSetFromMap(new WeakHashMap<>());
        players.addAll(Bukkit.getOnlinePlayers());
        return players;
    }

    public Observable<Set<Player>> getOnlinePlayersObservable() {
        return onlinePlayers;
    }

    public Observable<Integer> currentTickObservable() {
        return currentTick;
    }

    public Observable<Instant> getNow() {
        return now;
    }

    public void remove() {
        if (cprTask != null) {
            cprTask.cancel();
            cprTask = null;
        }

        if (playerQuitListener != null)
            HandlerList.unregisterAll(playerQuitListener);
        playerQuitListener = null;

        if (playerJoinListener != null)
            HandlerList.unregisterAll(playerJoinListener);
        playerJoinListener = null;

        onlinePlayers.clear();
        onlinePlayers = null;

        currentTick = null;
    }


}
