package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.0.46")
public interface BlockProp<T> {

    class Legacy {
        public static final String DATA = "LEGACY_DATA";
        public static final String SIGN_LINES = "LEGACY_SIGN_LINES";
    }

    String STAIRS_SHAPE = "STAIRS_SHAPE";
    String DIRECTIONAL = "DIRECTIONAL";
    String BAMBOO_LEAVES = "BAMBOO_LEAVES";
    String BED_PART = "BED_PART";
    String BEEHIVE_HONEY_LEVEL = "BEEHIVE_HONEY_LEVEL";
    String BELL_ATTACHMENT = "BELL_ATTACHMENT";
    String BIG_DRIP_LEAF_TILT = "BIG_DRIP_LEAF_TILT";
    String BREWING_STAND_BOTTLES = "BREWING_STAND_BOTTLES";
    String CAKE_BITES = "CAKE_BITES";
    String CAMPFIRE_SIGNAL_FIRE = "CAMPFIRE_SIGNAL_FIRE";
    String BUBBLE_COLUMN_DRAG = "BUBBLE_COLUMN_DRAG";

    Function<String[], ArrayStringBlockProp> ARRAY_STRING = ArrayStringBlockProp::new;

    T value();

}
