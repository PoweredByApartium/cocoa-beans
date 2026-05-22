package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TestPlayerVisibilityController implements PlayerVisibilityController {

    private int showPlayerCalls = 0;
    private int hidePlayerCalls = 0;
    private final Map<String, Integer> showPlayerCallsByPair = new HashMap<>();
    private final Map<String, Integer> hidePlayerCallsByPair = new HashMap<>();

    @Override
    public void showPlayer(JavaPlugin plugin, Player source, Player target) {
        showPlayerCalls++;
        showPlayerCallsByPair.merge(pairKey(source, target), 1, Integer::sum);
        source.showPlayer(plugin, target);
    }

    @Override
    public void hidePlayer(JavaPlugin plugin, Player source, Player target) {
        hidePlayerCalls++;
        hidePlayerCallsByPair.merge(pairKey(source, target), 1, Integer::sum);
        source.hidePlayer(plugin, target);
    }

    public int getShowPlayerCalls() {
        return showPlayerCalls;
    }

    public int getHidePlayerCalls() {
        return hidePlayerCalls;
    }

    public int getShowPlayerCalls(Player source, Player target) {
        return showPlayerCallsByPair.getOrDefault(pairKey(source, target), 0);
    }

    public int getHidePlayerCalls(Player source, Player target) {
        return hidePlayerCallsByPair.getOrDefault(pairKey(source, target), 0);
    }

    public void resetCounts() {
        showPlayerCalls = 0;
        hidePlayerCalls = 0;
        showPlayerCallsByPair.clear();
        hidePlayerCallsByPair.clear();
    }

    private static String pairKey(Player source, Player target) {
        return source.getUniqueId() + "->" + target.getUniqueId();
    }

}
