package net.apartium.cocoabeans.spigot.utils;

import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;

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
     * Determines if the specified version is a minecraft version
     * @see MinecraftVersion
     * @param version the specified version
     * @return true if a specified version is a minecraft version, otherwise false
     */
    private static boolean isMinecraftVersion(String version) {
        version = version.toLowerCase().replaceAll("v", "")
                .replaceAll("r", "");

        String[] args = version.split("_");
        if (args.length < 3) {
            return false;
        }

        MinecraftVersion minecraftVersion;
        try {
            minecraftVersion = MinecraftVersion.getVersion(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]));
        } catch (NumberFormatException e) {
            minecraftVersion = MinecraftVersion.UNKNOWN;
        }

        return minecraftVersion != MinecraftVersion.UNKNOWN;
    }

    /**
     * Formats the 'net.minecraft.server' path into a formatted path
     * @param path the provided path
     * @return the formatted path
     */
    public static String formatNMS(String path) {
        String serverVersion = getServerVersion();

        if (isMinecraftVersion(serverVersion)) {
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
        return String.format("%s.%s", getPackageName(), path);
    }
}
