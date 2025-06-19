package net.apartium.cocoabeans.spigot;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

/**
 @hidden
 */
@ApiStatus.Internal
public class SpigotServerCache {

    private static SpigotServerCache instance;

    private final String packageName;
    private final String serverVersion;

    public SpigotServerCache() {
        packageName = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = packageName.substring(packageName.lastIndexOf(".") + 1);
    }

    public static SpigotServerCache getInstance() {
        return instance == null ? (instance = new SpigotServerCache()) : instance;
    }

    public static void flush() {
        instance = null;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getServerVersion() {
        return serverVersion;
    }
}
