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
    private BukkitTask cprTask = null;
    private PlayerJoinListener playerJoinListener;
    private PlayerQuitListener playerQuitListener;


    private SetObservable<Player> onlinePlayers;
    private MutableObservable<Integer> currentTick;

    public SpigotProvidedState(JavaPlugin plugin) {
        this.plugin = plugin;

        this.onlinePlayers = Observable.set(getOnlinePlayers());
        this.currentTick = Observable.mutable(Bukkit.getCurrentTick());

        playerQuitListener = new PlayerQuitListener(onlinePlayers);
        Bukkit.getPluginManager().registerEvents(playerQuitListener, plugin);

        playerJoinListener = new PlayerJoinListener(onlinePlayers);
        Bukkit.getPluginManager().registerEvents(playerJoinListener, plugin);

    }

    public void heartbeat() {
        this.currentTick.set(Bukkit.getCurrentTick());
    }

    public void startCprTask() {
        startCprTask(1, 1);
    }

    public void startCprTask(long delay, long period) {
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
