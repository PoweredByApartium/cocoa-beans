package net.apartium.cocoabeans.spigot;

import org.jetbrains.annotations.ApiStatus;

/**
 * NMSUtils is a utility class for validating NMS/OBC class loading in runtime
 */
@ApiStatus.AvailableSince("0.0.39")
public class NMSUtils {

    private static final String
            NMS_PATH = "net.minecraft.server",
            OBC_PATH = "org.bukkit.craftbukkit";

    /**
     * Formats the 'net.minecraft.server' path into a formatted path
     * @param path the provided path
     * @return the formatted path
     */
    public static String formatNMS(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        String serverVersion = SpigotServerCache.getInstance().getServerVersion();

        if (SpigotServerCache.getInstance().containsVersion()) {
            String[] split = path.split("\\.");
            String className = split[split.length - 1];
            return String.format("%s.%s.%s", NMS_PATH, serverVersion, className);
        }

        return path.startsWith(NMS_PATH) ? path: String.format("%s.%s", NMS_PATH, path);
    }

    /**
     * Formats the 'org.bukkit.craftbukkit' path into a formatted path
     * @param path the provided path
     * @return the formatted path
     */
    public static String formatOBC(String path) {
        if (path == null || path.trim().isEmpty())
            throw new IllegalArgumentException("Path cannot be null or empty");

        if (path.startsWith(OBC_PATH))
            path = path.substring(OBC_PATH.length() + 1);

        return String.format("%s.%s", SpigotServerCache.getInstance().getPackageName(), path);
    }
}
