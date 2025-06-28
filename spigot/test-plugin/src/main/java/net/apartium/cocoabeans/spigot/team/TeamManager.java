package net.apartium.cocoabeans.spigot.team;

import net.apartium.cocoabeans.spigot.scoreboard.SpigotBoardPlayerGroup;
import net.apartium.cocoabeans.spigot.scoreboard.SpigotDisplayTeam;
import net.apartium.cocoabeans.scoreboard.team.DisplayTeam;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TeamManager {

    private final Map<String, SpigotDisplayTeam> teams = new HashMap<>();

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
        for (SpigotDisplayTeam team : teams.values())
            team.heartbeat();
    }

    public void disable() {
        if (cprTask != null)
            cprTask.cancel();

        cprTask = null;

        for (SpigotDisplayTeam team : teams.values())
            team.delete();

        teams.clear();
    }

    public DisplayTeam<Player> getTeam(String name) {
        return teams.computeIfAbsent(name, key -> new SpigotDisplayTeam(key, new SpigotBoardPlayerGroup(Collections.newSetFromMap(new WeakHashMap<>()))));
    }

    public Map<String, SpigotDisplayTeam> getTeams() {
        return Collections.unmodifiableMap(teams);
    }

}
