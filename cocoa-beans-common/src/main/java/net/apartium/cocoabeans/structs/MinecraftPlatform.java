package net.apartium.cocoabeans.structs;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a Minecraft platform, including its version, platform name, and platform version.
 * <p>
 * This record is used to encapsulate information about a specific Minecraft platform instance.
 * </p>
 * @param version the Minecraft version
 * @param platformName the name of the platform (e.g., Spigot, Minestom)
 * @param platformVersion the version of the platform
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public record MinecraftPlatform(
        MinecraftVersion version,
        String platformName,
        String platformVersion
) {

}
