package net.apartium.cocoabeans.spigot.utils;

import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public class NMSUtils {

    public static String getPackageName() {
        return Bukkit.getServer().getClass().getPackage().getName();
    }

    public static String getServerVersion() {
        String packageName = getPackageName();
        return packageName.substring(packageName.lastIndexOf(".") + 1);
    }

    public static boolean usePackageWithVersion() {
        try {
            Method getHandle = Bukkit.getServer().getClass().getMethod("getHandle");
            return getHandle.getReturnType().getName().contains(getServerVersion());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find method getHandle of class " +
                    Bukkit.getServer().getClass().getName(), e);
        }
    }

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

    public static String fixNMSFQDNForNonMappedFormat(String path) {
        String serverVersion = getServerVersion();
        if (usePackageWithVersion() && isMinecraftVersion(serverVersion)) {
            String[] split = path.split("\\.");
            String className = split[split.length - 1];
            return String.format("net.minecraft.server.%s.%s", serverVersion, className);
        }

        return path.startsWith("net.minecraft.server.") ? path
                : String.format("net.minecraft.server.%s", path);
    }

    public static String formatOBC(String path) {
        return String.format("%s.%s", getPackageName(), path);
    }
}
