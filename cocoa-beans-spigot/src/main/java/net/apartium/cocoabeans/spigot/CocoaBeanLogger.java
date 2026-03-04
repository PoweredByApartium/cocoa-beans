package net.apartium.cocoabeans.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.net.URLClassLoader;
import java.util.logging.Logger;

/**
 * @hidden
 */
@ApiStatus.Internal
public class CocoaBeanLogger {

    private static final Logger logger;

    static {
        if (CocoaBeanLogger.class.getClassLoader() instanceof URLClassLoader)
            logger = JavaPlugin.getProvidingPlugin(CocoaBeanLogger.class).getLogger();
        else
            logger = Bukkit.getPluginManager().getPlugins()[0].getLogger();
    }

    public static Logger getLogger() {
        return logger;
    }

    private CocoaBeanLogger() {}

}
