package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;
import net.apartium.cocoabeans.space.axis.Axis;

public interface FlipProp<T> {

    T flip(NamespacedKey type, Axis axis);

}
