package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.parsers.MapBasedParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.AvailableSince("0.0.39")
public class PluginParser extends MapBasedParser<Plugin> {

    public static final String DEFAULT_KEYWORD = "plugin";

    public PluginParser(String keyword, int priority) {
        super(keyword, Plugin.class, priority, true);
    }

    public PluginParser(int priority) {
        this(DEFAULT_KEYWORD, priority);
    }

    @Override
    public Map<String, Plugin> getMap() {
        // it's better to avoid caching at all to prevent memory leaks and other issues related to plugin loading and unloading
        Map<String, Plugin> plugins = new HashMap<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            plugins.put(plugin.getName().toLowerCase(), plugin);

        return plugins;
    }

}
