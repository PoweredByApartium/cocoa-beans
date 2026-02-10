package net.apartium.cocoabeans.structs;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a namespaced key, consisting of a namespace and a key string.
 * <p>
 * Used to uniquely identify resources or objects within a specific namespace, avoiding naming collisions.
 * Commonly used in Minecraft and similar systems for resource identification.
 * </p>
 * @param namespace the namespace part of the key (e.g., "minecraft", "apartium")
 * @param key the key part of the namespaced key (e.g., "stone", "custom_item")
 * @since 0.0.46
 */
@ApiStatus.AvailableSince("0.0.46")
public record NamespacedKey(String namespace, String key) {
}
