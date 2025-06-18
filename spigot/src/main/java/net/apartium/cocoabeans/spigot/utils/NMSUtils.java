package net.apartium.cocoabeans.spigot.utils;

import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Method;

/**
 * NMSUtils is a utility class for validating NMS/OBC class loading in runtime
 */
@ApiStatus.AvailableSince("0.0.39")
public class NMSUtils {

    /**
     * Gets the server's package name
     * @return the server package name
     */
    private static String getPackageName() {
        return Bukkit.getServer().getClass().getPackage().getName();
    }

    /**
     * Gets the server version format by the server package name
     * @return tne server version format
     */
    private static String getServerVersion() {
        String packageName = getPackageName();
        return packageName.substring(packageName.lastIndexOf(".") + 1);
    }

    /**
     * Determines if the getHandle method return type contains the specified version
     * @param version the specified version
     * @return true if getHandle method return type contains the specified version, otherwise false
     */
    private static boolean containsVersion(String version) {
        try {
            Method getHandle = Bukkit.getServer().getClass().getMethod("getHandle");
            return getHandle.getReturnType().getName().contains(version);
        } catch (NoSuchMethodException e) {
            String className = Bukkit.getServer().getClass().getName();
            throw new RuntimeException("Could not find method getHandle of class %s".formatted(className), e);
        }
    }

    /**
     * Formats the 'net.minecraft.server' path into a formatted path
     * @param path the provided path
     * @return the formatted path
     */
    public static String formatNMS(String path) {
        if (path == null || path.trim().isEmpty())
            throw new IllegalArgumentException("Path cannot be null or empty");

        String serverVersion = getServerVersion();

        if (containsVersion(serverVersion)) {
            String[] split = path.split("\\.");
            String className = split[split.length - 1];
            return String.format("net.minecraft.server.%s.%s", serverVersion, className);
        }

        return path.startsWith("net.minecraft.server.") ? path
                : String.format("net.minecraft.server.%s", path);
    }

    /**
     * Formats the 'org.bukkit.craftbukkit' path into a formatted path
     * @param path the provided path
     * @return the formatted path
     */
    public static String formatOBC(String path) {
        if (path == null || path.trim().isEmpty())
            throw new IllegalArgumentException("Path cannot be null or empty");

        return String.format("%s.%s", getPackageName(), path);
    }
}
