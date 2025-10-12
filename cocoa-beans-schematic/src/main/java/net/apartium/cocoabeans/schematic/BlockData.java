package net.apartium.cocoabeans.schematic;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.AvailableSince("0.0.45")
public interface BlockData {

    /**
     * Type of block
     * @return the type as namespace key
     */
    NamespacedKey type();

    /**
     * props are metadata for blocks
     * for example "facing"=Direction.NORTH
     * @return block props or empty if it has none
     */
    Map<String, BlockProp<?>> props();

}
