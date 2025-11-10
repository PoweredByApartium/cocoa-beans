package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface RotatableProp<T> {

    T rotate(NamespacedKey type, int degrees);

}
