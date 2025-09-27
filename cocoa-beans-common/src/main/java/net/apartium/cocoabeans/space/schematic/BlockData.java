package net.apartium.cocoabeans.space.schematic;

import java.util.Map;

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
