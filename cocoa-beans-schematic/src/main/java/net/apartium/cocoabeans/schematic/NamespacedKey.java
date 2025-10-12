package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

// todo probably move to common?
@ApiStatus.AvailableSince("0.0.45")
public record NamespacedKey(String namespace, String key) {
}
