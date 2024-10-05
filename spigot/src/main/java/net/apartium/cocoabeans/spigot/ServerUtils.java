/*
 * Copyright 2023 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot;

import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils to work with the running minecraft server
 */
public class ServerUtils {

    public static final String VERSION_PATTERN = "\\d+(\\.\\d+)*";

    private static final MinecraftVersion version = detectVersion();

    /**
     * Get game version of the running server
     * For example GameVersion(1, 8, 0)
     * @return game version, if not explicitly specified defaults to 0
     * @see MinecraftVersion
     */
    public static @NotNull MinecraftVersion getVersion() {
        return version;
    }

    @ApiStatus.Internal
    private static MinecraftVersion detectVersion() {
        String version = extractVersionNumber(Bukkit.getBukkitVersion());
        if (version == null) {
            return MinecraftVersion.UNKNOWN;
        } else {
            String[] split = version.split("\\.");
            return MinecraftVersion.getVersion(Integer.parseInt(split[0]), Integer.parseInt(split[1]), split.length == 2 ? 0 : Integer.parseInt(split[2]), getProtocolVersion());
        }
    }

    @ApiStatus.Internal
    private static int getProtocolVersion() {
        try {
            Class<?> constants = Class.forName("net.minecraft.SharedConstants");
            return (int) constants.getMethod("getProtocolVersion").invoke(null);
        } catch (Exception e) {
            return -1;
        }
    }

    private static String extractVersionNumber(String versionString) {
        Matcher matcher = Pattern.compile(VERSION_PATTERN).matcher(versionString);
        if (matcher.find()) return matcher.group();
        return null;
    }

}
