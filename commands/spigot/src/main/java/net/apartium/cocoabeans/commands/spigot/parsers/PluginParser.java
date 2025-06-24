package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.parsers.MapBasedParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.AvailableSince("0.0.39")
public class PluginParser extends MapBasedParser<Plugin> {

    public PluginParser(String keyword, int priority) {
        super(keyword, Plugin.class, priority);
    }

    public PluginParser(int priority) {
        this("plugin", priority);
    }

    @Override
    public Map<String, Plugin> getMap() {
        // it's better to avoid caching at all to prevent memory leaks and other issues related to plugin loading and unloading
        Map<String, Plugin> plugins = new HashMap<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            plugins.put(plugin.getName(), plugin);

        return plugins;
    }

}
