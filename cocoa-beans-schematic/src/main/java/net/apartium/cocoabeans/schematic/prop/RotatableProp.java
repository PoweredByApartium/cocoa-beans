package net.apartium.cocoabeans.schematic.prop;

import net.apartium.cocoabeans.structs.NamespacedKey;

public interface RotatableProp<T> {

    T rotate(NamespacedKey type, int degrees);

}
