package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.schematic.NamespacedKey;

public interface RotatableProp<T> {

    T rotate(NamespacedKey type, int degrees);

}
