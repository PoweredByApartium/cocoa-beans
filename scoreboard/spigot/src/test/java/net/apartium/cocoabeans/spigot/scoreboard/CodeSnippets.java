package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.scoreboard.team.DisplayTeam;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class CodeSnippets {

    public class TeamManager {

        private final Map<String, SpigotDisplayTeam> teams = new HashMap<>();

        private final SpigotBoardPlayerGroup group = new SpigotBoardPlayerGroup(Collections.newSetFromMap(new WeakHashMap<>()));

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

        public DisplayTeam<Player> getTeam(Rank rank) {
            return teams.computeIfAbsent(formatTeamName(
                    9999 - rank.getPriority(),
                    rank.name()
            ), key -> new SpigotDisplayTeam(key, group));
        }

        public void addViewer(Player player) {
            group.add(player);
        }

    }

    public enum Rank {
        DEFAULT(0, "§7"),
        VIP(10, "§a§lVIP "),
        HELPER(100, "§9§lHELPER "),
        ADMIN(300, "§4§lADMIN "),
        OWNER(500, "§c§lOWNER ");

        private final int priority;
        private final String prefix;

        Rank(int priority, String prefix) {
            this.priority = priority;
            this.prefix = prefix;
        }

        public int getPriority() {
            return priority;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public TeamManager teamManager;
    public TeamManager getTeamManager() {
        if (teamManager == null)
            teamManager = new TeamManager();

        return teamManager;
    }

    public Rank getRank(Player player) {
        return Rank.ADMIN;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TeamManager manager = getTeamManager();

        manager.getTeam(getRank(player))
                .addEntity(player.getName());
        manager.addViewer(player);
    }

    // We will want to call that at start
    public void createRankTeams() {
        TeamManager teamManager = getTeamManager();

        for (Rank rank : Rank.values())
            teamManager.getTeam(rank)
                    .setPrefix(Observable.immutable(Component.text(rank.getPrefix())));

    }

    /**
     * Formats an int priority into a Minecraft team name like "0001_Team"
     * @param priority The integer priority (e.g., 1, 2, 10).
     * @param name Optional team suffix or label (e.g., "Admin", "Mod"). Can be null or empty.
     * @return A formatted team name string like "0001_Admin".
     */
    public static String formatTeamName(int priority, String name) {
        String formated = String.format("%04d", priority);

        if (name == null || name.isEmpty())
            return formated;

        return formated + "_" + name;
    }
}
