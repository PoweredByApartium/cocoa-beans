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

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *
 */
public class SpigotProvidedState {

    private final JavaPlugin plugin;

    private SetObservable<Player> onlinePlayers;
    private MutableObservable<Integer> currentTick;

    private BukkitTask cprTask = null;

    private PlayerQuitListener playerQuitListener;
    private PlayerJoinListener playerJoinListener;

    public SpigotProvidedState(JavaPlugin plugin) {
        this.plugin = plugin;

        this.onlinePlayers = Observable.set(getOnlinePlayers());

        playerQuitListener = new PlayerQuitListener(this);
        Bukkit.getPluginManager().registerEvents(playerQuitListener, plugin);
        playerJoinListener = new PlayerJoinListener(this);
        Bukkit.getPluginManager().registerEvents(playerJoinListener, plugin);

        this.currentTick = Observable.mutable(Bukkit.getCurrentTick());
    }

    public void heartbeat() {
        this.currentTick.set(Bukkit.getCurrentTick());
    }

    public void createCprTask() {
        createCprTask(1, 1);
    }

    public void createCprTask(long delay, long period) {
        if (cprTask != null) {
            cprTask.cancel();
            cprTask = null;
        }

        cprTask = new BukkitRunnable() {

            @Override
            public void run() {
                heartbeat();
            }

        }.runTaskTimer(plugin, delay, period);
    }

    private Set<Player> getOnlinePlayers() {
        Set<Player> onlinePlayers = Collections.newSetFromMap(new WeakHashMap<>());
        onlinePlayers.addAll(Bukkit.getOnlinePlayers());
        return onlinePlayers;
    }

    public Observable<Set<Player>> getOnlinePlayersObservable() {
        return onlinePlayers;
    }

    public Observable<Integer> currentTickObservable() {
        return currentTick;
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
