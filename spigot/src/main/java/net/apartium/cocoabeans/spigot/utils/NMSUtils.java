package net.apartium.cocoabeans.spigot.utils;

import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public class NMSUtils {

    private final static String
            PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName(),
            SERVER_VERSION = PACKAGE_NAME.substring(PACKAGE_NAME.lastIndexOf(".") + 1);

    private static final boolean USE_PACKAGE_WITH_VERSION = shouldUsePackageWithVersion();

    private static boolean shouldUsePackageWithVersion() {
        try {
            Method getHandle = Bukkit.getServer().getClass().getMethod("getHandle");
            return getHandle.getReturnType().getName().contains(SERVER_VERSION);
        } catch (NoSuchMethodException e) {
            String className = Bukkit.getServer().getClass().getName();
            throw new RuntimeException("Could not find method getHandle of class %s".formatted(className), e);
        }
    }

    private static boolean isMinecraftVersion(String version) {
        version = version.toLowerCase().replaceAll("v", "")
                .replaceAll("r", "");

        String[] args = version.split("_");
        MinecraftVersion minecraftVersion;
        try {
            minecraftVersion = MinecraftVersion.getVersion(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]));
        } catch (NumberFormatException e) {
            minecraftVersion = MinecraftVersion.UNKNOWN;
        }

        return minecraftVersion != MinecraftVersion.UNKNOWN;
    }

    public static String fixNMSFQDNForNonMappedFormat(String newFQDN) {
        if (USE_PACKAGE_WITH_VERSION && isMinecraftVersion(SERVER_VERSION)) {
            String[] split = newFQDN.split("\\.");
            String className = split[split.length - 1];
            return String.format("net.minecraft.server.%s.%s", SERVER_VERSION, className);
        }

        return String.format("net.minecraft.server.%s", newFQDN);
    }

    public static String formatOBC(String path) {
        return String.format("%s.%s", PACKAGE_NAME, path);
    }
}
