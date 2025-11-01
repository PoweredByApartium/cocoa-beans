package net.apartium.cocoabeans.structs;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public record NamespacedKey(String namespace, String key) {
}
