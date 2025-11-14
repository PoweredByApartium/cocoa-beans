package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a rotation aware property value.
 * When a schematic is rotated, the rotate method is called, which should return the rotated value
 * @param <T> property type
 */
@ApiStatus.AvailableSince("0.0.46")
@NullMarked
public interface RotatableProp<T> {

    /**
     * Returns the rotated value of the property
     * @param type property key
     * @param degrees degrees to rotate by
     * @return a rotated property instance
     */
    T rotate(NamespacedKey type, int degrees);

}
