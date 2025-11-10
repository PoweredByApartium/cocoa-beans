package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.space.axis.Axis;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public interface FlipProp<T> {

    T flip(NamespacedKey type, Axis axis);

}
