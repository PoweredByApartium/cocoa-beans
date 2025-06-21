package net.apartium.cocoabeans.spigot;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.TestOnly;

import java.lang.reflect.Method;

/**
 @hidden
 */
@ApiStatus.Internal
public class ServerInfoStore {

    private static ServerInfoStore instance;

    private final String packageName;
    private final String serverVersion;

    private boolean containsVersion;

    public ServerInfoStore() {
        packageName = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = packageName.substring(packageName.lastIndexOf(".") + 1);
    }

    public static ServerInfoStore getInstance() {
        return instance == null ? (instance = new ServerInfoStore()) : instance;
    }

    @TestOnly
    public static void flush() {
        instance = null;
    }

    public String getOBCPackageName() {
        return packageName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public boolean containsVersion() {
        if (this.containsVersion) {
            return true;
        }

        try {
            Method method = Bukkit.getServer().getClass().getMethod("getHandle");
            this.containsVersion = method.getReturnType().getName().contains(serverVersion);
            return containsVersion;
        } catch (NoSuchMethodException e) {
            String className = Bukkit.getServer().getClass().getName();
            throw new RuntimeException("Could not find method getHandle of class %s".formatted(className), e);
        }
    }
}
