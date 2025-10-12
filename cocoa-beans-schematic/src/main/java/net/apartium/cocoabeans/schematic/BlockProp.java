package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.45")
public interface BlockProp<T> {

    T value();

}
