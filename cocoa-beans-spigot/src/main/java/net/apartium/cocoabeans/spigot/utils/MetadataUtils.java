package net.apartium.cocoabeans.spigot.utils;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * Utility class for working with Bukkit metadata.
 */
public class MetadataUtils {

    /**
     * Retrieves a metadata value associated with the specified key from a Metadatable object.
     *
     * @param <T>           the type of the metadata value
     * @param metadatable   the Metadatable object from which to retrieve the metadata value
     * @param metadataKey   the key of the metadata value
     * @param clazz         the class type of the metadata value
     * @param plugin        the Plugin instance that owns the metadata
     * @return an Optional containing the metadata value if found, or an empty Optional if not found or if the value is not of the specified class type
     */
    public static <T> Optional<T> getMetadataValue(Metadatable metadatable, String metadataKey, Class<T> clazz, Plugin plugin) {
        return metadatable.getMetadata(metadataKey).stream()
                .filter(metadataValue -> metadataValue.getOwningPlugin() == plugin)
                .map(MetadataValue::value)
                .filter(clazz::isInstance)
                .map(clazz::cast).findFirst();
    }

}
