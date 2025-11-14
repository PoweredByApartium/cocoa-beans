package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.space.axis.Axis;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a flip aware prop
 * @see RotatableProp
 * @param <T> property type
 */
@ApiStatus.AvailableSince("0.0.46")
public interface FlippableProp<T> {

    /**
     * Calculates the flipped property value
     * @param type property key
     * @param axis flip on axis
     * @return new property value
     */
    T flip(NamespacedKey type, Axis axis);

}
