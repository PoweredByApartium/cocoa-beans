package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

/**
 * Represents a string block property
 * @param value string value
 * @see BlockProp
 */
@ApiStatus.AvailableSince("0.0.46")
public record StringBlockProp(@NonNull String value) implements BlockProp<String> {

}
