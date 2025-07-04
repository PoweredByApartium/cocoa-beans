package net.apartium.cocoabeans.spigot;

import net.apartium.cocoabeans.Ensures;
import org.jetbrains.annotations.ApiStatus;

/**
 * General utility methods for interacting with NMS
 */
@ApiStatus.AvailableSince("0.0.40")
public class NMSUtils {

    private static final String
            NMS_PATH = "net.minecraft.server",
            OBC_PATH = "org.bukkit.craftbukkit";

    private NMSUtils() {

    }

    /**
     * Formats the 'net.minecraft.server' path into a formatted path
     * @param path the provided path
     * @return the formatted path
     */
    public static String formatNMS(String path) {
        Ensures.isFalse(path == null || path.trim().isEmpty(), new IllegalArgumentException("Path cannot be null or empty"));

        // Older server versions use obfuscated package structure.
        // The entire NMS package is flat, so we must adapt accordingly.
        if (ServerInfoStore.getInstance().containsVersion()) {
            String[] split = path.split("\\.");
            String className = split[split.length - 1];
            String serverVersion = ServerInfoStore.getInstance().getServerVersion();
            return String.format("%s.%s.%s", NMS_PATH, serverVersion, className);
        }

        return path.startsWith(NMS_PATH) ? path : String.format("%s.%s", NMS_PATH, path);
    }

    /**
     * Formats the 'org.bukkit.craftbukkit' path into a formatted path
     * @param path the provided path
     * @return the formatted path
     */
    public static String formatOBC(String path) {
        Ensures.isFalse(path == null || path.trim().isEmpty(), new IllegalArgumentException("Path cannot be null or empty"));
        Ensures.isFalse(path.equals(OBC_PATH), new IllegalArgumentException("Not enough information to format path"));

        if (path.startsWith(OBC_PATH))
            path = path.substring(OBC_PATH.length() + 1);

        return String.format("%s.%s", ServerInfoStore.getInstance().getOBCPackageName(), path);
    }
}
